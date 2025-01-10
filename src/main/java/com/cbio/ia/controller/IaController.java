package com.cbio.ia.controller;

import com.cbio.ia.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/ia")
@RestController
public class IaController {

    private final OpenAIService openAIService;


    @PostMapping("/query")
    public String index(@RequestBody String chat) {
        return openAIService.getHintToChat(chat);
    }


}