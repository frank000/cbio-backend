package com.cbio.app.service;

import com.cbio.core.service.CanalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ValidacaoCallbackService{

    public CanalService canalService;


    public boolean validaToken(String nomeCanal, String token) throws Exception {

        log.info("\n\tVALIDACAO_CALLBACK -  Token: {}", token);

        return canalService.existsByTokenAndCliente(token, nomeCanal);
    }


}
