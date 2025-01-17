package com.cbio.app.entities;

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

    private String status;

    private LocalDateTime createdAt;
}

