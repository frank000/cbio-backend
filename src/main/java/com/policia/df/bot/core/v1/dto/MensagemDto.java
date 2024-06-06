package com.policia.df.bot.core.v1.dto;

import lombok.Data;

@Data
public class MensagemDto {

    private Long chatId;

    private Long messagemId;

    private Long timestamp;

    private String text;

    private String sessao;

    public String getEnvio() {
        return new StringBuilder()
                .append("{\"chat_id\":\"")
                .append(getChatId())
                .append("\",\"text\":\"")
                .append(getText())
                .append("\"}")
                .toString();
    }

    public String montarEnvio(String text, Long chatId) {
        return new StringBuilder()
                .append("{\"chat_id\":\"")
                .append(chatId)
                .append("\",\"text\":\"")
                .append(text)
                .append("\"}")
                .toString();
    }

}
