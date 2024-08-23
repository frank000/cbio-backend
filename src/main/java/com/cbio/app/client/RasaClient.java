package com.cbio.app.client;

import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.RasaMessageOutDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name="rasaClient", url = "${assistent.rasa.url}")
public interface RasaClient {

    @PostMapping(value = "webhooks/rest/webhook", consumes = {"application/json"})
    List<RasaMessageDTO> webhook(RasaMessageOutDTO rasaMessageDTO);


}
