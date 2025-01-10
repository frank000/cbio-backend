package com.cbio.app.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.service.utils.HashUtils;
import com.cbio.core.service.CanalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ValidateCallbackService {

    public CanalService canalService;

    public boolean validaToken(String nomeCanal, String token) throws Exception {

        log.info("\n\tVALIDACAO_CALLBACK -  Token: {}", token);

        return canalService.existsByTokenAndCliente(token, nomeCanal);
    }

    public boolean validaHashFacebookEToken(String signture, String payload, String canal, String from, String token) throws Exception {
        signture = signture.replace("sha256=", "");
        Optional<CanalEntity> canalByTokenAndCliente = canalService.findCanalByTokenAndCliente(canal, token);
        String hash = HashUtils.getHash(payload, canalByTokenAndCliente.orElseThrow().getApiKey());
        return signture.equals(hash);
    }

//    public boolean validaHashFacebook(String signture, String payload) throws Exception {
//        signture = signture.replace("sha256=", "");
//        String hash = HashUtils.getHash(payload, this.facebookAppToken);
//        return signture.equals(hash);
//    }


}
