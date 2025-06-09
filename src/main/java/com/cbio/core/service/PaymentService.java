package com.cbio.core.service;


import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.CheckoutRequest;
import com.cbio.core.v1.dto.RequestDTO;
import com.cbio.core.v1.dto.SubscriptionDTO;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public interface PaymentService {
    Map<String, String> createCheckoutSession(CheckoutRequest request) throws StripeException;
    Map<String, String> createTrialSubscription(RequestDTO requestDTO) throws StripeException;
    void handlePaymentSuccess(String sessionId, HttpServletResponse response) throws StripeException, IOException;
    void handlePaymentFailure(String sessionId, HttpServletResponse response) throws StripeException, IOException;
    String handleWebhookEvent(String payload, String sigHeader);
    Map<String, String> cancelSubscription(String subscriptionId, String reason) throws StripeException, CbioException;
    SubscriptionDTO getSubscription(String subscriptionId) throws StripeException, CbioException;

}