package com.cbio.app.entities;

import com.cbio.core.v1.dto.EventSessionDTO;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Getter(AccessLevel.NONE)
    private List<InvoiceDTO> invoice;

    @Indexed
    private String subscriptionId;

    private String companyId;


    private String reason;

    @Getter(AccessLevel.NONE)
    private Boolean active;

    public Boolean getActive() {
        if (active==null) {
            active = Boolean.TRUE;
        }
        return active;
    }


    public List<InvoiceDTO> getInvoice() {
        if(invoice == null){
            invoice = new ArrayList<>();
        }
        return invoice;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceDTO{

        private String invoiceId;

        private String urlHostedInvoice;

        private String urlInvoicePdf;

        private LocalDateTime date;
    }
}

