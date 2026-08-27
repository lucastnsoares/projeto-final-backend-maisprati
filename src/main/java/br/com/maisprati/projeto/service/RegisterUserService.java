package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.AuthForgotPasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.UserCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.UserRegisterRequestDTO;
import br.com.maisprati.projeto.dto.response.UserSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.UserRepository;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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
    private final PasswordResetTokenService passwordResetTokenService;

    @Transactional
    public UserSummaryResponseDTO registerUserPublic(UserRegisterRequestDTO dto) {
        userExists(dto.email(),dto.document());

        User newUser = new User();
        newUser.setRole(Collections.singleton(Role.DOADOR));
        newUser.setName(dto.name().toUpperCase());
        newUser.setDocument(dto.document().replaceAll("[.\\-/]", "").trim().toUpperCase());
        newUser.setEmail(dto.email().toLowerCase());
        newUser.setPasswordHash(passwordEncoder.encode(dto.password()));
        newUser.setPhone(formatToE164(dto.phone()));

        userRepository.saveAndFlush(newUser);

        return new UserSummaryResponseDTO(newUser);
    }

    @Transactional
    public UserResponseDTO registerUserAdmin(UserCreateRequestDTO dto) {
        userExists(dto.email(),dto.document());

        User newUser = new User();
        newUser.setRole(dto.roles());
        newUser.setName(dto.name().toUpperCase());
        newUser.setDocument(dto.document().replaceAll("[.\\-/]", "").trim().toUpperCase());
        newUser.setEmail(dto.email().toLowerCase());
        newUser.setPasswordHash(null);
        newUser.setPhone(formatToE164(dto.phone()));

        User savedUser = userRepository.saveAndFlush(newUser);

        passwordResetTokenService.forgotPassword(new AuthForgotPasswordRequestDTO(savedUser.getEmail()));

        return new UserResponseDTO(newUser);
    }


    private void userExists(String email, String document) {
        if (userRepository.existsByDocument(document)) throw new IllegalArgumentException("Documento já cadastrado.");
        if (userRepository.existsByEmail(email)) throw new IllegalArgumentException("E-mail já utilizado");
    }

    private String formatToE164(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return null;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(rawPhone.trim(), null);
            return phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164); // Retorna sempre ex: +5531987654321
        } catch (NumberParseException e) {
            return rawPhone.replaceAll("[^0-9+]", "");
        }
    }
}
