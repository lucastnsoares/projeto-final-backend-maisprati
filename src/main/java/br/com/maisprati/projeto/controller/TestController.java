package br.com.maisprati.projeto.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
@RequestMapping("/test")
public class TestController {
    @GetMapping("/public")
    public String publicRoute() {
        return "Você está em um endpoint público.";
    }

    @GetMapping("/restrict")
    public String authenticatedRoute() {
        return "Você está em uma rota autenticada.";
    }

    @GetMapping("/restrict/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String onlyAdminRoute() {
        return "Você está em uma rota autenticada. APENAS ADMINISTRADORES.";
    }
}
