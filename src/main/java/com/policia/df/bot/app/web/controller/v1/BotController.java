package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.app.entities.UserEntity;
import com.policia.df.bot.core.service.BotService;
import com.policia.df.bot.core.service.CanalService;
import com.policia.df.bot.core.v1.dto.MensagemDto;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/bot")
public record BotController(BotService service, CanalService canalService) {

    @PostMapping(value = "/connect")
    ResponseEntity<Void> connect(
            @RequestParam("token")  String token,
            @org.springframework.web.bind.annotation.RequestBody Update update

    ) throws Exception {
        if(!token.equals("asdf")){
            throw new Exception("Forbidden");
        } else {
            service.connectToBot(update);
            //salvar banco o token configuração
            //id from
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/enviar-mensagem")
    Object sendMessage(@org.springframework.web.bind.annotation.RequestBody MensagemDto mensagem) throws IOException {

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = okhttp3.RequestBody.create(mediaType, mensagem.getEnvio());

        return service.sendMessage(body);
    }

    @GetMapping(value = "/listar-todos-canais")
    List<CanalEntity> listTabCanal() {
        return canalService.listarTodos();
    }


}
