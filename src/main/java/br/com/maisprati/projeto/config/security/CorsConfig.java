package br.com.maisprati.projeto.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens permitidas (URLs do frontend local ou em produção)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000", // Padrão React / Next.js
                "http://localhost:5173", // Padrão Vite
                "http://localhost:5500", // Padrão LiveServer
                "http://127.0.0.1:*"
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Cabeçalhos HTTP permitidos
        config.setAllowedHeaders(List.of("*"));

        // Permite envio de credenciais/cookies
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
