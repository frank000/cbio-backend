package com.cbio.core.v1.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    private String customerEmail;
    private String customerName;
    private String priceId; // ID do preço no Stripe (ex: price_1ABCDEF...)
    private String successUrl;
    private String cancelUrl;
}
