package br.com.maisprati.projeto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from:}")
    private String fromEmail;

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
}
