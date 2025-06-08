package com.cbio.app.entities;

import com.cbio.core.v1.dto.EventSessionDTO;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("checkoutSession")
public class CheckoutSessionEntity {

    @Id
    private String id;

    private String sessionId;

    private String customerId;

    private String name;

    private  String email;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime paidAt;

    private EventSessionDTO.CustomerDetails customerDetails;

    private String urlHostedInvoice;
    private String urlInvoicePdf;

    private String subscriptionId;

}

