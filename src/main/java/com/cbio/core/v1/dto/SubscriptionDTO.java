package com.cbio.core.v1.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionDTO implements Serializable {
    private String subscriptionId;

    private String companyId;

    private String urlHostedInvoice;

    private String urlInvoicePdf;

    private EventSessionDTO.CustomerDetails customerDetails;

    private String name;

    private  String email;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime paidAt;
}
