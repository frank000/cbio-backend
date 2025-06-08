package com.cbio.app.web.controller.v1;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.CheckoutSessionEntity;
import com.cbio.app.entities.PlanEntity;
import com.cbio.app.entities.StatusPaymentEnum;
import com.cbio.app.repository.CheckoutSessionRepository;
import com.cbio.app.repository.PlanRepository;
import com.cbio.app.service.utils.CustomerUtil;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.EmailService;
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
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequestMapping("/v1/payment")
@RestController
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PlanRepository planRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final AuthService authService;
    private final CompanyService companyService;
    private final EmailService emailService;

    @Value("${stripe.api.key}")
    private String STRIPE_API_KEY;

    @Value("${app.client-base-url}")
    private String clientBaseURL;

    @Value("${app.public-base-url}")
    private String publicBaseURL;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${app.external-front-url}")
    private String frontExternalUrl;


    public PaymentController(PlanRepository planRepository, CheckoutSessionRepository checkoutSessionRepository, AuthService authService, CompanyService companyService, EmailService emailService, EmailService emailService1) {
        this.planRepository = planRepository;
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.authService = authService;
        this.companyService = companyService;
        this.emailService = emailService1;
    }


    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
        Stripe.apiKey = STRIPE_API_KEY;

        try {

            Map<String, Object> claimsUserLogged = authService.getClaimsUserLogged();
            String name = claimsUserLogged.get("name").toString();
            String email = claimsUserLogged.get("email").toString();
            Customer customer = CustomerUtil.findOrCreateCustomer(email, name);


            CheckoutSessionEntity checkoutSession = new CheckoutSessionEntity();
            checkoutSession.setCustomerId(customer.getId());
            checkoutSession.setStatus("pending");
            checkoutSession.setName(name);
            checkoutSession.setEmail(email);
            checkoutSession.setCreatedAt(LocalDateTime.now());
            checkoutSession = checkoutSessionRepository.save(checkoutSession);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(String.format("%s/v1/payment/success?session_id={CHECKOUT_SESSION_ID}", clientBaseURL))
                    .setCancelUrl(String.format("%s/v1/payment/failure?session_id={CHECKOUT_SESSION_ID}", clientBaseURL))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(request.getPriceId())
                                    .setQuantity(1L)
                                    .build())
                    .build();

            Session session = Session.create(params);


            checkoutSession.setSessionId(session.getId());
            checkoutSessionRepository.save(checkoutSession);


            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/subscriptions/trial")
    public ResponseEntity<Map<String, String>> newSubscriptionWithTrial(@RequestBody RequestDTO requestDTO, HttpServletResponse response) throws StripeException, IOException {

        Stripe.apiKey = STRIPE_API_KEY;

        // Start by finding existing customer record from Stripe or creating a new one if needed
        Customer customer = CustomerUtil.findOrCreateCustomer(requestDTO.getCustomerEmail(), requestDTO.getCustomerName());

        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(clientBaseURL + "/v1/payment/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(publicBaseURL + "/failure-payment")
                        // For trials, you need to set the trial period in the session creation request
                        .setSubscriptionData(SessionCreateParams.SubscriptionData.builder().setTrialPeriodDays(30L).build());

        for (Product product : requestDTO.getItems()) {
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

        Session session = Session.create(paramsBuilder.build());

        Map<String, String> map = new HashMap<>();
        map.put("url", session.getUrl());

        CheckoutSessionEntity checkoutSessionEntity = CheckoutSessionEntity.builder()
                .customerId(customer.getId())
                .status("created")
                .sessionId(session.getId())
                .createdAt(CbioDateUtils.LocalDateTimes.now())
                .build();
        checkoutSessionRepository.save(checkoutSessionEntity);

        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/failure")
    public void failPayment(
            @RequestParam("session_id") String session_id,
            HttpServletResponse response) throws IOException, StripeException {

        // 1. Encontre a sessão no banco pelo sessionId do Stripe
        CheckoutSessionEntity checkoutSession = checkoutSessionRepository
                .findBySessionId(session_id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 2. Verifique o status REAL via API Stripe (opcional)
        Session session = Session.retrieve(session_id);
        if ("complete".equals(session.getStatus())) {
            checkoutSession.setStatus("failed");
            checkoutSession.setUpdatedAt(CbioDateUtils.LocalDateTimes.now());
            checkoutSessionRepository.save(checkoutSession);
        }

        // 3. Redirecione
        response.sendRedirect(publicBaseURL + "/failed-payment/");
    }


    @GetMapping("/success")
    public void successPayment(
            @RequestParam("session_id") String session_id,
            HttpServletResponse response) throws IOException, StripeException {

        // 1. Encontre a sessão no banco pelo sessionId do Stripe
        CheckoutSessionEntity checkoutSession = checkoutSessionRepository
                .findBySessionId(session_id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 2. Verifique o status REAL via API Stripe (opcional)
        Session session = Session.retrieve(session_id);
        if ("complete".equals(session.getStatus())) {
            checkoutSession.setStatus("completed");

            checkoutSessionRepository.save(checkoutSession);
        }

        // 3. Redirecione
        response.sendRedirect(publicBaseURL + "/success-payment/");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            log.info(event.getType());
            if ("checkout.session.completed".equals(event.getType())) {

                ResultSessionAndCheckout result = getResultSessionAndCheckout(event);

                // Marca como pago apenas se o pagamento foi bem-sucedido
                if ("paid".equals(result.session().getPaymentStatus())) {

                    String companyIdUserLogged = authService.getCompanyIdUserLogged();
                    companyService.changeStatusPayment(companyIdUserLogged, StatusPaymentEnum.MENSAL);

                    result.checkoutSession().setCustomerDetails(result.session().getCustomerDetails());
                    result.checkoutSession().setStatus("paid");
                    result.checkoutSession().setSubscriptionId((String)result.session().getSubscription());

                    result.checkoutSession().setPaidAt(CbioDateUtils.LocalDateTimes.now());
                    checkoutSessionRepository.save(result.checkoutSession());

                    // Disparar e-mail, etc.
                }
            } else if ("invoice.updated".equals(event.getType())) {

                ObjectMapper mapper = new ObjectMapper();

                Map<String, Object> eventMap = mapper.readValue(
                        event.getData().toJson(),
                        new TypeReference<Map<String, Object>>() {
                        }
                );

                Map<String, Object> previousAttributes = (Map<String, Object>) eventMap.get("previous_attributes");
                Map<String, Object> objectData = (Map<String, Object>) eventMap.get("object");
                Map<String, Object> objectParent = (Map<String, Object>) objectData.get("parent");

                // Exemplo de acesso a valores
                String subscriptionID = (String) ((Map<String, Object>)objectParent.get("subscription_details")).get("subscription");

                Map<String, Object> model = new HashMap<>();
                model.put("nome", (String) objectData.get("customer_name"));
                model.put("invoiceId", (String) objectData.get("id"));
                model.put("urlInvoice", (String) objectData.get("hosted_invoice_url"));
                model.put("urlPDF", (String) objectData.get("invoice_pdf"));
                model.put("urlCancelamento", frontExternalUrl +"/cancelation-subscription/" + subscriptionID );


                emailService.enviarEmailModel(
                        (String) objectData.get("customer_email"),
                        "RayzaTEC - Seu comprovante de pagamento foi emitido",
                        "email-invoice.ftlh",
                        model);

            }else if("customer.subscription.deleted".equals(event.getType())) {

                Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().get();

                // Atualize seu banco de dados
                checkoutSessionRepository.findBySubscriptionId(subscription.getId())
                        .ifPresent(session -> {
                            session.setStatus("canceled");
                            session.setUpdatedAt(CbioDateUtils.LocalDateTimes.now());
                            checkoutSessionRepository.save(session);
                        });
            }


            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing webhook");
        }
    }
    @NotNull
    private ResultSessionAndCheckout getResultSessionAndCheckout(Event event) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        EventDTO events = mapper.readValue(event.getData().toJson(), EventDTO.class);
        EventSessionDTO session = events.getCheckoutSession();

        CheckoutSessionEntity checkoutSession = checkoutSessionRepository
                .findBySessionId(session.getId())
                .orElseThrow(() -> new RuntimeException("Session not found"));
        ResultSessionAndCheckout result = new ResultSessionAndCheckout(session, checkoutSession);
        return result;
    }

    private record ResultSessionAndCheckout(EventSessionDTO session, CheckoutSessionEntity checkoutSession) {
    }
    @PostMapping("/subscriptions/cancel")
    public ResponseEntity<?> cancelSubscription(
            @RequestParam String subscriptionId) {

        try {
            // 1. Cancelar no Stripe
            Subscription subscription = Subscription.retrieve(subscriptionId);
            Subscription canceledSubscription = subscription.cancel();

            // 2. Atualizar status no banco de dados
            Optional<CheckoutSessionEntity> sessionOpt = checkoutSessionRepository
                    .findBySubscriptionId(subscriptionId);

            if (sessionOpt.isPresent()) {
                CheckoutSessionEntity session = sessionOpt.get();
                session.setStatus("canceled");
                session.setUpdatedAt(LocalDateTime.now());
                checkoutSessionRepository.save(session);
            }

            // 3. Registrar o cancelamento na sua tabela de assinaturas (se tiver)
            // subscriptionService.cancel(subscriptionId);

            return ResponseEntity.ok(Map.of(
                    "status", "canceled",
                    "message", "Assinatura cancelada com sucesso"
            ));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
