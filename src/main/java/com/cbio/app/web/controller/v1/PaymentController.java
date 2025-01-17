package com.cbio.app.web.controller.v1;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.CheckoutSessionEntity;
import com.cbio.app.entities.PlanEntity;
import com.cbio.app.repository.CheckoutSessionRepository;
import com.cbio.app.repository.PlanRepository;
import com.cbio.app.service.utils.CustomerUtil;
import com.cbio.core.v1.dto.RequestDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/v1/payment")
@RestController
public class PaymentController {

    private final PlanRepository planRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;

    @Value("${stripe.api.key}")
    private String STRIPE_API_KEY;

    @Value("${app.client-base-url}")
    private String clientBaseURL;

    @Value("${app.public-base-url}")
    private String publicBaseURL;

    public PaymentController(PlanRepository planRepository, CheckoutSessionRepository checkoutSessionRepository) {
        this.planRepository = planRepository;
        this.checkoutSessionRepository = checkoutSessionRepository;
    }


    @PostMapping("/checkout/hosted")
    String hostedCheckout(@RequestBody RequestDTO requestDTO) throws StripeException {
        return "Hello World!";
    }

    @PostMapping("/subscriptions/trial")
    public ResponseEntity< Map<String, String>> newSubscriptionWithTrial(@RequestBody RequestDTO requestDTO, HttpServletResponse response) throws StripeException, IOException {

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
                    .orElseThrow(()-> new RuntimeException("Plano não encontrado."));
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


    @GetMapping("/success")
    void sucessPayment(@RequestParam(value = "session_id") String sessionId, HttpServletResponse response) throws IOException {
        CheckoutSessionEntity checkoutSessionEntity = checkoutSessionRepository.findBySessionId(sessionId).orElseThrow();
        checkoutSessionEntity.setStatus("completed");
        checkoutSessionRepository.save(checkoutSessionEntity);
        //enviar email com senha
        //Pedir completar o cadastro

        response.sendRedirect(publicBaseURL+"/success-payment/");

    }



}
