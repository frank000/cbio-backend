package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSessionDTO implements Serializable {
    private String id;

    @JsonProperty("object")
    private String objectType;

    @JsonProperty("adaptive_pricing")
    private AdaptivePricing adaptivePricing;

    @JsonProperty("after_expiration")
    private Object afterExpiration;

    @JsonProperty("allow_promotion_codes")
    private Object allowPromotionCodes;

    @JsonProperty("amount_subtotal")
    private long amountSubtotal;

    @JsonProperty("amount_total")
    private long amountTotal;

    @JsonProperty("automatic_tax")
    private AutomaticTax automaticTax;

    @JsonProperty("billing_address_collection")
    private Object billingAddressCollection;

    @JsonProperty("cancel_url")
    private String cancelUrl;

    @JsonProperty("client_reference_id")
    private Object clientReferenceId;

    @JsonProperty("client_secret")
    private Object clientSecret;

    @JsonProperty("collected_information")
    private Object collectedInformation;

    @JsonProperty("consent")
    private Object consent;

    @JsonProperty("consent_collection")
    private Object consentCollection;

    private long created;
    private String currency;

    @JsonProperty("currency_conversion")
    private Object currencyConversion;

    @JsonProperty("custom_fields")
    private List<Object> customFields;

    @JsonProperty("custom_text")
    private CustomText customText;

    private Object customer;

    @JsonProperty("customer_creation")
    private String customerCreation;

    @JsonProperty("customer_details")
    private CustomerDetails customerDetails;

    @JsonProperty("customer_email")
    private Object customerEmail;

    private List<Object> discounts;

    @JsonProperty("expires_at")
    private long expiresAt;

    private Object invoice;

    @JsonProperty("invoice_creation")
    private InvoiceCreation invoiceCreation;

    private boolean livemode;
    private Object locale;
    private Map<String, Object> metadata;
    private String mode;

    @JsonProperty("payment_intent")
    private String paymentIntent;

    @JsonProperty("payment_link")
    private Object paymentLink;

    @JsonProperty("payment_method_collection")
    private String paymentMethodCollection;

    @JsonProperty("payment_method_configuration_details")
    private PaymentMethodConfigurationDetails paymentMethodConfigurationDetails;

    @JsonProperty("payment_method_options")
    private PaymentMethodOptions paymentMethodOptions;

    @JsonProperty("payment_method_types")
    private List<String> paymentMethodTypes;

    @JsonProperty("payment_status")
    private String paymentStatus;

    private Object permissions;

    @JsonProperty("phone_number_collection")
    private PhoneNumberCollection phoneNumberCollection;

    @JsonProperty("recovered_from")
    private Object recoveredFrom;

    @JsonProperty("saved_payment_method_options")
    private Object savedPaymentMethodOptions;

    @JsonProperty("setup_intent")
    private Object setupIntent;

    @JsonProperty("shipping_address_collection")
    private Object shippingAddressCollection;

    @JsonProperty("shipping_cost")
    private Object shippingCost;

    @JsonProperty("shipping_details")
    private Object shippingDetails;

    @JsonProperty("shipping_options")
    private List<Object> shippingOptions;

    private String status;

    @JsonProperty("submit_type")
    private Object submitType;

    private Object subscription;

    @JsonProperty("success_url")
    private String successUrl;

    @JsonProperty("total_details")
    private TotalDetails totalDetails;

    @JsonProperty("ui_mode")
    private String uiMode;

    private Object url;

    @JsonProperty("wallet_options")
    private Object walletOptions;

    // Inner classes
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdaptivePricing implements Serializable {
        private boolean enabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AutomaticTax implements Serializable {
        private boolean enabled;
        private Object liability;
        private Object provider;
        private Object status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomText implements Serializable {
        @JsonProperty("after_submit")
        private Object afterSubmit;

        @JsonProperty("shipping_address")
        private Object shippingAddress;

        private Object submit;

        @JsonProperty("terms_of_service_acceptance")
        private Object termsOfServiceAcceptance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetails implements Serializable {
        private Address address;
        private String email;
        private String name;
        private Object phone;

        @JsonProperty("tax_exempt")
        private String taxExempt;

        @JsonProperty("tax_ids")
        private List<Object> taxIds;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Address implements Serializable {
            private String city;
            private String country;

            @JsonProperty("line1")
            private String line1;

            @JsonProperty("line2")
            private Object line2;

            @JsonProperty("postal_code")
            private String postalCode;

            private String state;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceCreation implements Serializable {
        private boolean enabled;

        @JsonProperty("invoice_data")
        private InvoiceData invoiceData;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class InvoiceData implements Serializable {
            @JsonProperty("account_tax_ids")
            private Object accountTaxIds;

            @JsonProperty("custom_fields")
            private Object customFields;

            private Object description;
            private Object footer;
            private Object issuer;
            private Map<String, Object> metadata;

            @JsonProperty("rendering_options")
            private Object renderingOptions;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodConfigurationDetails implements Serializable {
        private String id;
        private Object parent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodOptions implements Serializable {
        private Card card;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Card implements Serializable {
            @JsonProperty("request_three_d_secure")
            private String requestThreeDSecure;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneNumberCollection implements Serializable {
        private boolean enabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalDetails implements Serializable {
        @JsonProperty("amount_discount")
        private long amountDiscount;

        @JsonProperty("amount_shipping")
        private long amountShipping;

        @JsonProperty("amount_tax")
        private long amountTax;
    }

}