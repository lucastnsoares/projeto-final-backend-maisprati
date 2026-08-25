package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;

import java.time.Instant;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String name,
        String document,
        String email,
        String phone,
        Set<Role> roles,
        Instant createdAt,
        Instant updatedAt
) {

    public UserResponseDTO(User user){
        this(
                user.getId(),
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

