package com.cbio.app.service;

import com.cbio.core.service.EmailService;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freeMarkerConfigurer;

    public EmailServiceImpl(JavaMailSender mailSender, FreeMarkerConfigurer freeMarkerConfigurer) {
        this.mailSender = mailSender;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }

    public void enviarEmailSimples(String para, String assunto, String conteudo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom("noreplay@rayzatec.com.br");
            mensagem.setTo(para);
            mensagem.setSubject(assunto);
            mensagem.setText(conteudo);

            mailSender.send(mensagem);
            log.info(String.format("MAIL SERVICE: ok, to: %s, subject: %s",  para, assunto));
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarEmailHtml(String para, String assunto, String conteudoHtml)
            throws MessagingException {

        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setFrom("noreplay@rayzatec.com.br");
        helper.setTo(para);
        helper.setSubject(assunto);
        helper.setText(conteudoHtml, true); // true indica que é HTML

        mailSender.send(mensagem);
        log.info(String.format("MAIL SERVICE: ok, to: %s, subject: %s", para, assunto));
    }

    @Override
    public void enviarEmailModel(String para, String assunto, String template, Map<String, Object> model) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Processa o template FreeMarker
            Template freemarkerTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(template);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, model);

            helper.setFrom("noreplay@rayzatec.com.br");
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(html, true); // true indica que é HTML

            mailSender.send(message);
            log.info(String.format("MAIL SERVICE: ok, to: %s, subject: %s", para, assunto));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar email", e);
        }
    }

}