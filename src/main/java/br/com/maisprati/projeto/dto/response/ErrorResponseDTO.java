package br.com.maisprati.projeto.dto.response;

import java.time.Instant;

public record ErrorResponseDTO(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
){}
