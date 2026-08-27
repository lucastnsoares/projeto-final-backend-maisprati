package br.com.maisprati.projeto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    public void sendEmailTest(String email, String token) {
        String message = """
                ******************************** TOKEN DE REDEFINIÇÃO DE SENHA ************************
                -> DESTINATÁRIO: %s
                -> TOKEN: %s
                ****************************************************************************************
                """.formatted(email, token);
        System.out.println(message);
    }
}
