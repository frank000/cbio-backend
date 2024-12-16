package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document("ticket")
@Getter
@Setter
@Builder
public class TicketEntity {


    @Id
    private String id;

    private String title;

    private String protocolNumber;

    private List<TicketMessageDTO> ticketMessages;

    private String type;

    private Boolean ativo;

    private Boolean fromCompany;

    private CompanyDTO company;

    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Builder
    public static class TicketMessageDTO implements Serializable {
        private String message;

        private String userId;

        @JsonFormat(locale = "pt-BR", shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        private LocalDateTime createdAt;
    }
}
