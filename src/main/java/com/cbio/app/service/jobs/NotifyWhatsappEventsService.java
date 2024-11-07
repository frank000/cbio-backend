package com.cbio.app.service.jobs;

import com.cbio.core.service.ResourceService;
import com.cbio.core.service.SessaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotifyWhatsappEventsService {

    private final ResourceService resourceService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void closeSession() {
        log.info("NOTIFY WHATSAPP - start");
        resourceService.notifyByConfigNotification();

    }

}
