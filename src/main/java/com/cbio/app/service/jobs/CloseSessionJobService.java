package com.cbio.app.service.jobs;

import com.cbio.core.service.SessaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloseSessionJobService {

    private final SessaoService sessaoService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void closeSession() {
        log.info("CLOSE SESSION - Closing session");

        Long numberDocs = sessaoService.closeAttendaceLastOneHour();

        log.info(String.format("CLOSE SESSION - Closed : %s sessions", numberDocs));

        Long numberDocsClosedWindow = sessaoService.closeAttendaceWhatsappCloseWindow();

        log.info(String.format("CLOSE SESSION - Closed Window - Whatsapp : %s sessions", numberDocsClosedWindow));
    }

}
