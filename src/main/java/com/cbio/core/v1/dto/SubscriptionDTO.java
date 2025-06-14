package com.cbio.core.v1.dto;

import com.cbio.app.entities.CheckoutSessionEntity;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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

    private List<CheckoutSessionEntity.InvoiceDTO> invoice;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime paidAt;
}
