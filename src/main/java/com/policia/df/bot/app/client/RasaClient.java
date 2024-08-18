package com.policia.df.bot.app.client;

import com.policia.df.bot.core.v1.dto.RasaMessageDTO;
import com.policia.df.bot.core.v1.dto.RasaMessageOutDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name="rasaClient", url = "${assistent.rasa.url}")
public interface RasaClient {

    @PostMapping(value = "webhooks/rest/webhook", consumes = {"application/json"})
    List<RasaMessageDTO> webhook(RasaMessageOutDTO rasaMessageDTO);
    //Object webhook(RasaMessageOutDTO rasaMessageDTO);
//

}
//
