package br.com.maisprati.projeto.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserChangePasswordRequestDTO(
        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
){}
