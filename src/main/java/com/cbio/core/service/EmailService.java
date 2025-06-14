package com.cbio.core.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {

    void enviarEmailSimples(String para, String assunto, String conteudo);
    void enviarEmailHtml(String para, String assunto, String conteudoHtml)
            throws MessagingException;


    void enviarEmailModel(String para, String assunto,String template, Map<String, Object> model)
            throws MessagingException;
}
