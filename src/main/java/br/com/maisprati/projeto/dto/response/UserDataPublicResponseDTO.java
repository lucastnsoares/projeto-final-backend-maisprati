package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.User;

import java.time.Instant;

public record UserDataPublicResponseDTO(
        String name,
        String document,
        String email,
        String phone,
        Instant createdAt
) {
    public UserDataPublicResponseDTO(User user) {
        this(
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt());
    }
}
