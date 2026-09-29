package br.com.maisprati.projeto.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:}")
    private String fromEmail;
    
    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @Value("${app.mail.enabled}")
    private boolean isMailEnabled;

    @Async
    public void sendSimpleEmail(String to, String subject, String content) {
        if (!isMailEnabled){
            log.info("""
                    
                    ****** SIMULAÇÃO E-MAIL REDEFINIÇÃO SENHA ******
                    PARA: {}
                    ASSUNTO: {}
                    CONTEÚDO:
                    {}
                    ******************************************************
                    """, to, subject, content);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            log.info("E-mail enviado com sucesso para: {}", to);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para: {}", to, e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!isMailEnabled) {
            log.info("Simulando envio de e-mail HTML para: {}", to);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            
            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }
            
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setText(htmlContent, true);
            
            javaMailSender.send(message);
            log.info("E-mail HTML enviado com sucesso para: {}", to);
        } catch (MessagingException e) {
            log.error("Falha ao enviar e-mail HTML para: {}", to, e);
            throw new RuntimeException("Erro ao enviar e-mail HTML", e);
        }
    }

    @Async
    public void sendPasswordResetEmailHtml(String to, String subject, String name, String token, String expirationTime) {
        Map<String, Object> variables = Map.of(
                "nome", name,
                "tempoExpiracao", expirationTime + " minutos",
                "linkRedefinicao", frontEndUrl + "/redefinir-senha/nova?token=" + token
        );
        sendHtmlEmail(to, subject, "email-reset-password", variables);
    }
}
