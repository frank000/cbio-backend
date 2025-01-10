package com.cbio.core.v1.dto;

import com.cbio.app.entities.TicketEntity;
import com.cbio.app.service.enuns.StatusTicketsEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class TicketDTO implements Serializable {
    private String id;

    private String title;

    private String protocolNumber;

    private String ticketMessage;

    private List<TicketEntity.TicketMessageDTO> ticketMessages;

    private String type;

    private String userId;

    private Boolean ativo;

    private Boolean fromCompany = false;

    private StatusTicketsEnum status;

    private CompanyDTO company;

    @JsonFormat(locale = "pt-BR", shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    private LocalDateTime createdAt;
}
