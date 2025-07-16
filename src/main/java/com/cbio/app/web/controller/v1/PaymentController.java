package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.CheckoutSessionEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.grid.SubscriptionGridRepository;
import com.cbio.core.service.PaymentService;
import com.cbio.core.v1.dto.CheckoutRequest;
import com.cbio.core.v1.dto.RequestDTO;
import com.cbio.core.v1.dto.SubscriptionDTO;
import com.cbio.core.v1.dto.SubscriptionFiltroGridDTO;
import com.stripe.exception.StripeException;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequestMapping("/v1/payment")
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final SubscriptionGridRepository subscriptionGridRepository;

    public PaymentController(PaymentService paymentService, SubscriptionGridRepository subscriptionGridRepository) {
        this.paymentService = paymentService;
        this.subscriptionGridRepository = subscriptionGridRepository;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) throws StripeException {
        Map<String, String> response = paymentService.createCheckoutSession(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscriptions/trial")
    public ResponseEntity<Map<String, String>> newSubscriptionWithTrial(
            @RequestBody RequestDTO requestDTO,
            HttpServletResponse response) throws IOException, StripeException {
        Map<String, String> result = paymentService.createTrialSubscription(requestDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/failure")
    public void failPayment(
            @RequestParam("session_id") String session_id,
            HttpServletResponse response) throws IOException, StripeException {
        paymentService.handlePaymentFailure(session_id, response);
    }

    @GetMapping("/success")
    public void successPayment(
            @RequestParam("session_id") String session_id,
            HttpServletResponse response) throws IOException, StripeException {
        paymentService.handlePaymentSuccess(session_id, response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        String result = paymentService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subscriptions/cancel")
    public ResponseEntity<?> cancelSubscription(@RequestParam String subscriptionId, @RequestParam String reason) throws StripeException, CbioException {
        Map<String, String> result = paymentService.cancelSubscription(subscriptionId, reason);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<?> getSubscription(@PathVariable String id) throws StripeException, CbioException {
        SubscriptionDTO result = paymentService.getSubscription(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/grid")
    public ResponseEntity<?> getAllSubscription( @RequestParam(required = false) final String filter,
                                                 @RequestParam(required = false) final String perfil,
                                                 @RequestParam(required = false) final String companyId,
                                                 @RequestParam(defaultValue = "0") Integer pageIndex,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(defaultValue = "id") String sortField,
                                                 @RequestParam(defaultValue = "DESC") String sortType)
 throws CbioException {

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        SubscriptionFiltroGridDTO gridFiltroDTO = SubscriptionFiltroGridDTO.builder()
                .busca(filter)
                .idCompany(companyId)
                .build();

        PageableResponse<SubscriptionDTO> list = subscriptionGridRepository.obtemGrid(gridFiltroDTO, pageable, CheckoutSessionEntity.class, SubscriptionDTO.class);

        return ResponseEntity.ok(list);
    }
}
