package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.UserRegisterRequestDTO;
import br.com.maisprati.projeto.dto.response.UserSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RegisterUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSummaryResponseDTO registerUserPublic(UserRegisterRequestDTO dto) {
        userExists(dto.email(),dto.document());

        User newUser = new User();
        newUser.setRole(Collections.singleton(Role.DOADOR));
        newUser.setName(dto.name());
        newUser.setDocument(dto.document());
        newUser.setEmail(dto.email());
        newUser.setPasswordHash(passwordEncoder.encode(dto.password()));
        newUser.setPhone(dto.phone());

        userRepository.save(newUser);

        return new UserSummaryResponseDTO(newUser);
    }

    @Transactional
    public UserResponseDTO registerUserAdmin(UserCreateRequestDTO dto) {
        userExists(dto.email(),dto.document());

        User newUser = new User();
        newUser.setRole(dto.roles());
        newUser.setName(dto.name().toUpperCase());
        newUser.setDocument(dto.document());
        newUser.setEmail(dto.email().toLowerCase());
        newUser.setPasswordHash(passwordEncoder.encode(dto.password()));
        newUser.setPhone(dto.phone());

        userRepository.save(newUser);

        return new UserResponseDTO(newUser);
    }


    private void userExists(String email, String document) {
        if (userRepository.existsByDocument(document)) throw new IllegalArgumentException("Documento já cadastrado.");
        if (userRepository.existsByEmail(email)) throw new IllegalArgumentException("E-mail já utilizado");
    }
}
