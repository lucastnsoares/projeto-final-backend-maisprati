package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.model.enums.Role;

import java.util.Set;

public record UserUpdateRequestDTO(
        String name,

        String document,

        String email,

        String phone,

        Set<Role> roles,

        Boolean isActive
) { }
