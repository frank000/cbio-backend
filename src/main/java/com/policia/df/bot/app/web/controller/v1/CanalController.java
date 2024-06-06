package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.service.CanalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/canal")
public record CanalController(CanalService service) {

    @PostMapping(value = "/incluir")
    public CanalEntity incluirCanal(@RequestBody CanalEntity canal) {

        return service.incluirCanal(canal);

    }

}
