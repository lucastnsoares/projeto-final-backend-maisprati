package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserCreateRequestDTO(
        @NotBlank
        String name,

        @NotBlank
        String document,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String phone,

        @NotEmpty
        Set<Role> roles
) { }
