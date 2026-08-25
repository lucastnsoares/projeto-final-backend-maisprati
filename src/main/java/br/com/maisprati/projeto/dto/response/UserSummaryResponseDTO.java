package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.User;

import java.time.Instant;

public record UserSummaryResponseDTO(
        String name,
        String document,
        String email,
        String phone,
        Instant createdAt
) {
    public UserSummaryResponseDTO(User user) {
        this(
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt());
    }
}
