package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Retorna o token JWT do usuário logado")
public record TokenResponseDTO(
        String token
) {}
