package com.policia.df.bot.app.service.serder;

import com.policia.df.bot.core.v1.dto.DialogoDTO;

public interface Sender {
    void envia(DialogoDTO dialogoDTO);
}
