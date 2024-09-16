package com.cbio.core.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.GitlabEventDTO;
import okhttp3.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface ProcessableService {

    void processaMensagem(EntradaMensagemDTO entradaMensagemDTO, CanalEntity canalEntity);
}
