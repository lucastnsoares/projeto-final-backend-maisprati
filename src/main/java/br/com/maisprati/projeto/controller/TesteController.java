package br.com.maisprati.projeto.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
@RequestMapping("/api/teste")
public class TesteController {
    @GetMapping("/publico")
    public String rotaPublica(){
        return "Você está em um endpoint público.";
    }

    @GetMapping("/restrito")
    public String rotaAutenticada(){
        return "Você está em uma rota autenticada.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String rotaApenasAdmin() {
        return "Você está em uma rota autenticada. APENAS ADMINISTRADORES.";
    }
}
