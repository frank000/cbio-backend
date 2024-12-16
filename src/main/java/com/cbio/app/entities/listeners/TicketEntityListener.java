package com.cbio.app.entities.listeners;

import com.cbio.app.entities.TicketEntity;
import com.cbio.app.service.ProtocolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TicketEntityListener extends AbstractMongoEventListener<TicketEntity> {

    @Autowired
    private ProtocolServiceImpl protocolService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<TicketEntity> event) {
        TicketEntity entity = event.getSource();
        if (entity.getCreatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
        }

        if (entity.getProtocolNumber() == null) {
            LocalDateTime now = LocalDateTime.now();
            entity.setProtocolNumber(protocolService.generateProtocolNumber(String.valueOf(now.getYear())));
        }
    }

}