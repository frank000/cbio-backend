package com.cbio.app.service;


import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.CheckoutSessionEntity;
import com.cbio.app.entities.PlanEntity;
import com.cbio.app.entities.StatusPaymentEnum;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.CheckoutSessionRepository;
import com.cbio.app.repository.PlanRepository;
import com.cbio.app.service.utils.CustomerUtil;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.EmailService;
import com.cbio.core.service.PaymentService;
import com.cbio.core.v1.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PlanRepository planRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final AuthService authService;
    private final CompanyService companyService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${app.client-base-url}")
    private String clientBaseURL;

    @Value("${app.public-base-url}")
    private String publicBaseURL;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${app.external-front-url}")
    private String frontExternalUrl;

    public PaymentServiceImpl(PlanRepository planRepository,
                              CheckoutSessionRepository checkoutSessionRepository,
                              AuthService authService,
                              CompanyService companyService,
                              EmailService emailService,
                              ObjectMapper objectMapper) {
        this.planRepository = planRepository;
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.authService = authService;
        this.companyService = companyService;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public Map<String, String> createCheckoutSession(CheckoutRequest request) throws StripeException {
        Map<String, Object> claimsUserLogged = authService.getClaimsUserLogged();
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        if (companyIdUserLogged != null) {
            Stripe.apiKey = stripeApiKey;
            String name = claimsUserLogged.get("name") != null ? claimsUserLogged.get("name").toString() : "";
            String email = claimsUserLogged.get("email") != null ? claimsUserLogged.get("email").toString() : "";
            Customer customer = CustomerUtil.findOrCreateCustomer(email, name);

            CheckoutSessionEntity checkoutSession = createCheckoutSessionEntity(customer, name, email);
            checkoutSession.setCompanyId(companyIdUserLogged);
            checkoutSession.setActive(Boolean.TRUE);

            SessionCreateParams params = buildSessionParams(request.getPriceId());
            Session session = Session.create(params);
            updateCheckoutSessionWithStripeId(checkoutSession, session.getId());

            return Map.of("sessionId", session.getId());

        } else {
            log.error("Checkout precisa de ume empresa para ser completo.");
            return null;
        }

    }

    @Override
    public Map<String, String> createTrialSubscription(RequestDTO requestDTO) throws StripeException {
        Customer customer = CustomerUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());
        SessionCreateParams.Builder paramsBuilder = buildTrialSessionParams(customer);

        for (Product product : requestDTO.getItems()) {
            addProductToSession(paramsBuilder, product);
        }

        Session session = Session.create(paramsBuilder.build());
        createCheckoutSessionRecord(customer, session);

        return Map.of("url", session.getUrl());
    }

    @Override
    public void handlePaymentSuccess(String sessionId, HttpServletResponse response) throws StripeException, IOException {
        CheckoutSessionEntity checkoutSession = getAndValidateSession(sessionId);
        Session session = Session.retrieve(sessionId);

        if ("complete".equals(session.getStatus())) {
            updateCheckoutSessionStatus(checkoutSession, "completed");
        }

        response.sendRedirect(publicBaseURL + "/success-payment/");
    }

    @Override
    public void handlePaymentFailure(String sessionId, HttpServletResponse response) throws StripeException, IOException, IOException {
        CheckoutSessionEntity checkoutSession = getAndValidateSession(sessionId);
        Session session = Session.retrieve(sessionId);

        if ("complete".equals(session.getStatus())) {
            updateCheckoutSessionStatus(checkoutSession, "failed");
        }

        response.sendRedirect(publicBaseURL + "/failed-payment/");
    }

    @Override
    public String handleWebhookEvent(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            log.info("Received Stripe event: {}", event.getType());

            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                case "invoice.updated":
                    handleInvoiceUpdated(event);
                    break;
                case "customer.subscription.deleted":
                    handleSubscriptionDeleted(event);
                    break;
                case "invoice_payment.paid":
                    log.info("Received Stripe event: invoice_payment");

                    break;
                case "invoice.paid":
                    log.info("Received Stripe event: invoice.paid");
                    handleInvoicePaid(event);
                    break;
                default:
                    log.debug("Unhandled event type: {}", event.getType());
            }

            return "Webhook processed successfully";
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            throw new RuntimeException("Error processing webhook", e);
        }
    }

    @Override
    public Map<String, String> cancelSubscription(String subscriptionId, String reason) throws StripeException, CbioException {
        try {
            RequestOptions requestOptions = RequestOptions.builder()
                    .setApiKey(stripeApiKey)
                    .build();
            Subscription subscription = Subscription.retrieve(subscriptionId, requestOptions);
            Subscription canceledSubscription = subscription.cancel();

            updateSubscriptionStatusInDatabase(subscriptionId, reason);


            return Map.of(
                    "status", "canceled",
                    "message", "Assinatura cancelada com sucesso"
            );
        } catch (StripeException e) {

            if ("resource_missing".equals(e.getCode())) {
                throw new CbioException("Inscrição não encontrada na Stripe. Abra um ticket para nossa equipe de atendimento.", HttpStatus.BAD_REQUEST.value());
            } else {
                throw new CbioException("Problema generico. Abra um ticket para nossa equipe de atendimento.", HttpStatus.BAD_REQUEST.value());
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SubscriptionDTO getSubscription(String subscriptionId) throws StripeException, CbioException {
        CheckoutSessionEntity checkoutSessionEntity = checkoutSessionRepository.findBySubscriptionId(subscriptionId).orElseThrow(() -> new CbioException("Assinatura não encontrada. Abra um ticket para nosso time de administradores.", HttpStatus.NOT_FOUND.value()));

        return getSubscriptionDTO(checkoutSessionEntity);
    }

    private static SubscriptionDTO getSubscriptionDTO(CheckoutSessionEntity checkoutSessionEntity) {
        return SubscriptionDTO.builder()
                .subscriptionId(checkoutSessionEntity.getSubscriptionId())
                .companyId(checkoutSessionEntity.getCompanyId())
                .customerDetails(checkoutSessionEntity.getCustomerDetails())
                .paidAt(checkoutSessionEntity.getPaidAt())
                .updatedAt(checkoutSessionEntity.getUpdatedAt())
                .invoice(checkoutSessionEntity.getInvoice())
                .name(checkoutSessionEntity.getName())
                .email(checkoutSessionEntity.getEmail())
                .status(checkoutSessionEntity.getStatus())
                .build();
    }

    private CheckoutSessionEntity createCheckoutSessionEntity(Customer customer, String name, String email) {
        CheckoutSessionEntity checkoutSession = new CheckoutSessionEntity();
        checkoutSession.setCustomerId(customer.getId());
        checkoutSession.setStatus("pending");
        checkoutSession.setName(name);
        checkoutSession.setEmail(email);
        checkoutSession.setCreatedAt(CbioDateUtils.LocalDateTimes.now());
        return checkoutSessionRepository.save(checkoutSession);
    }

    private SessionCreateParams buildSessionParams(String priceId) {

        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(String.format("%s/v1/payment/success?session_id={CHECKOUT_SESSION_ID}", clientBaseURL))
                .setCancelUrl(String.format("%s/v1/payment/failure?session_id={CHECKOUT_SESSION_ID}", clientBaseURL))
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build())
                .build();
    }

    private void updateCheckoutSessionWithStripeId(CheckoutSessionEntity checkoutSession, String sessionId) {
        checkoutSession.setSessionId(sessionId);
        checkoutSessionRepository.save(checkoutSession);
    }

    private SessionCreateParams.Builder buildTrialSessionParams(Customer customer) {
        Stripe.apiKey = stripeApiKey;
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(customer.getId())
                .setSuccessUrl(clientBaseURL + "/v1/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(publicBaseURL + "/failure-payment")
                .setSubscriptionData(SessionCreateParams.SubscriptionData.builder().setTrialPeriodDays(30L).build());
    }

    private void addProductToSession(SessionCreateParams.Builder paramsBuilder, Product product) {
        PlanEntity planEntity = planRepository.findByType(product.getId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado."));
        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                PriceData.builder()
                                        .setProductData(
                                                PriceData.ProductData.builder()
                                                        .putMetadata("app_id", product.getId())
                                                        .setName(product.getName())
                                                        .build()
                                        )
                                        .setCurrency(planEntity.getProduct().getDefaultPrice().getCurrency())
                                        .setUnitAmountDecimal(planEntity.getProduct().getDefaultPrice().getUnitAmountDecimal())
                                        .setRecurring(PriceData.Recurring.builder().setInterval(PriceData.Recurring.Interval.MONTH).build())
                                        .build())
                        .build());
    }

    private void createCheckoutSessionRecord(Customer customer, Session session) {
        CheckoutSessionEntity checkoutSessionEntity = CheckoutSessionEntity.builder()
                .customerId(customer.getId())
                .status("created")
                .sessionId(session.getId())
                .createdAt(CbioDateUtils.LocalDateTimes.now())
                .build();
        checkoutSessionRepository.save(checkoutSessionEntity);
    }

    private CheckoutSessionEntity getAndValidateSession(String sessionId) {
        return checkoutSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    private void updateCheckoutSessionStatus(CheckoutSessionEntity checkoutSession, String status) {
        checkoutSession.setStatus(status);
        checkoutSession.setUpdatedAt(CbioDateUtils.LocalDateTimes.now());
        checkoutSessionRepository.save(checkoutSession);
    }

    private void handleCheckoutSessionCompleted(Event event) throws JsonProcessingException, CbioException {
        ResultSessionAndCheckout result = getResultSessionAndCheckout(event);

        if ("paid".equals(result.session().getPaymentStatus())) {

            Map<String, Object> eventMap = objectMapper.readValue(
                    event.getData().toJson(),
                    new TypeReference<Map<String, Object>>() {
                    }
            );
//            String idSession = result.session().getId();

            Map<String, Object> objectData = (Map<String, Object>) eventMap.get("object");
            String sessionId = (String) objectData.get("id");

            companyService.changeStatusPayment(result.checkoutSession().getCompanyId(), StatusPaymentEnum.MENSAL);

            result.checkoutSession().setCustomerDetails(result.session().getCustomerDetails());
            result.checkoutSession().setStatus("paid");
            result.checkoutSession().setSubscriptionId((String) result.session().getSubscription());
            result.checkoutSession().setPaidAt(CbioDateUtils.LocalDateTimes.now());
            checkoutSessionRepository.save(result.checkoutSession());
        }
    }

    private void handleInvoiceUpdated(Event event) throws JsonProcessingException, MessagingException {
        Map<String, Object> eventMap = objectMapper.readValue(
                event.getData().toJson(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> objectData = (Map<String, Object>) eventMap.get("object");
        Map<String, Object> objectParent = (Map<String, Object>) objectData.get("parent");
        String subscriptionID = (String) ((Map<String, Object>) objectParent.get("subscription_details")).get("subscription");


        String invoceId = (String) objectData.get("id");
        String hostedInvoiceUrl = (String) objectData.get("hosted_invoice_url");
        String invoicePdf = (String) objectData.get("invoice_pdf");

        Optional<CheckoutSessionEntity> bySubscriptionId = checkoutSessionRepository.findBySubscriptionId(subscriptionID);
        if (bySubscriptionId.isPresent()) {

            bySubscriptionId.get().getInvoice()
                    .add(
                            CheckoutSessionEntity.InvoiceDTO.builder()
                                    .invoiceId(invoceId)
                                    .urlHostedInvoice(hostedInvoiceUrl)
                                    .urlInvoicePdf(invoicePdf)
                                    .date(CbioDateUtils.LocalDateTimes.now())
                                    .totalAmount(Integer.parseInt((String) objectData.get("amount_paid")))
                                    .customer((String) objectData.get("customer"))
                                    .build()
                    );


            checkoutSessionRepository.save(bySubscriptionId.get());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("nome", (String) objectData.get("customer_name"));
        model.put("invoiceId", invoceId);
        model.put("urlInvoice", hostedInvoiceUrl);
        model.put("urlCancelamento", frontExternalUrl + "/cancelation-subscription/" + subscriptionID);

        emailService.enviarEmailModel(
                (String) objectData.get("customer_email"),
                "RayzaTEC - Seu comprovante de pagamento foi emitido",
                "email-invoice.ftlh",
                model);
    }


    private void handleInvoicePaid(Event event) throws JsonProcessingException, MessagingException {
        Map<String, Object> eventMap = objectMapper.readValue(
                event.getData().toJson(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> objectData = (Map<String, Object>) eventMap.get("object");
        Map<String, Object> objectParent = (Map<String, Object>) objectData.get("parent");
        String subscriptionID = (String) ((Map<String, Object>) objectParent.get("subscription_details")).get("subscription");


        String invoceId = (String) objectData.get("id");
        String hostedInvoiceUrl = (String) objectData.get("hosted_invoice_url");
        String invoicePdf = (String) objectData.get("invoice_pdf");

        Optional<CheckoutSessionEntity> bySubscriptionId = checkoutSessionRepository.findBySubscriptionId(subscriptionID);
        if (bySubscriptionId.isPresent()) {

            bySubscriptionId.get().getInvoice()
                    .add(
                            CheckoutSessionEntity.InvoiceDTO.builder()
                                    .invoiceId(invoceId)
                                    .urlHostedInvoice(hostedInvoiceUrl)
                                    .urlInvoicePdf(invoicePdf)
                                    .date(CbioDateUtils.LocalDateTimes.now())
                                    .totalAmount(Integer.parseInt((String) objectData.get("amount_paid")))
                                    .customer((String) objectData.get("customer"))
                                    .build()
                    );

            bySubscriptionId.get().getEvent()
                    .add(
                            CheckoutSessionEntity.EventStripeDTO.builder()
                                    .eventId(event.getId())
                                    .number((String) objectData.get("number"))
                                    .subscription(subscriptionID)
                                    .date(CbioDateUtils.LocalDateTimes.now())
                                    .build()
                    );

            checkoutSessionRepository.save(bySubscriptionId.get());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("nome", (String) objectData.get("customer_name"));
        model.put("invoiceId", invoceId);
        model.put("urlInvoice", hostedInvoiceUrl);
        model.put("urlCancelamento", frontExternalUrl + "/cancelation-subscription/" + subscriptionID);

        emailService.enviarEmailModel(
                (String) objectData.get("customer_email"),
                "RayzaTEC - Seu comprovante de pagamento foi emitido",
                "email-invoice.ftlh",
                model);
    }


    private void handleSubscriptionDeleted(Event event) throws JsonProcessingException {
        Map<String, Object> eventMap = objectMapper.readValue(
                event.getData().toJson(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> objectData = (Map<String, Object>) eventMap.get("object");
        String subscriptionID = (String) objectData.get("id");


        checkoutSessionRepository.findBySubscriptionId(subscriptionID)
                .ifPresent(session -> {
                    session.setStatus("canceled");
                    session.setUpdatedAt(CbioDateUtils.LocalDateTimes.now());
                    checkoutSessionRepository.save(session);

                    CompanyDTO companyDTO = companyService.findById(session.getCompanyId());

                    companyDTO.setDataAlteracaoStatus(CbioDateUtils.LocalDateTimes.now());
                    companyDTO.setStatusPayment(StatusPaymentEnum.CANCELLED);
                    try {
                        companyService.save(companyDTO);
                    } catch (CbioException e) {
                        log.error("Error while saving company DTO - cancelling event stripe", e);
                    }

                });

    }

    private ResultSessionAndCheckout getResultSessionAndCheckout(Event event) throws JsonProcessingException {
        EventDTO events = objectMapper.readValue(event.getData().toJson(), EventDTO.class);
        EventSessionDTO session = events.getCheckoutSession();

        CheckoutSessionEntity checkoutSession = checkoutSessionRepository
                .findBySessionId(session.getId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return new ResultSessionAndCheckout(session, checkoutSession);
    }

    private void updateSubscriptionStatusInDatabase(String subscriptionId, String reason) throws MessagingException {
        Optional<CheckoutSessionEntity> sessionOpt = checkoutSessionRepository
                .findBySubscriptionId(subscriptionId);

        if (sessionOpt.isPresent()) {
            CheckoutSessionEntity session = sessionOpt.get();
            session.setStatus("canceled");
            LocalDateTime now = CbioDateUtils.LocalDateTimes.now();

            session.setUpdatedAt(now);
            session.setReason(reason);
            checkoutSessionRepository.save(session);


            LocalDateTime nowPLusThirty = now.plusDays(30);


            sendMailCancelling(session, now, nowPLusThirty);
        }
    }

    private void sendMailCancelling(CheckoutSessionEntity session, LocalDateTime now, LocalDateTime nowPLusThirty) throws MessagingException {
        CompanyDTO companyDTO = companyService.findById(session.getCompanyId());
        Map<String, Object> model = new HashMap<>();
        model.put("companyName", companyDTO.getNome());
        model.put("tier", companyDTO.getTier());
        model.put("dataCancelamento", CbioDateUtils.getDateTimeWithSecFormated(now, CbioDateUtils.FORMAT_BRL_DATE_TIME));
        model.put("dataFinalAcesso", CbioDateUtils.getDateTimeWithSecFormated(nowPLusThirty, CbioDateUtils.FORMAT_BRL_DATE_TIME));


        emailService.enviarEmailModel(
                session.getEmail(),
                "Inscrição cancelada - RayzaTEC",
                "email-cancel.ftlh",
                model
        );
    }

    private record ResultSessionAndCheckout(EventSessionDTO session, CheckoutSessionEntity checkoutSession) {
    }
}