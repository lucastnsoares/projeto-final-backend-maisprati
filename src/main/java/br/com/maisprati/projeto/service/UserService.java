package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserChangePasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.UserEditProfileRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.UserUpdatedResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.UserRepository;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    @Transactional
    public void changePassword(User loggedInUser, UserChangePasswordRequestDTO dto){
        User user = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));
        if (!encoder.matches(dto.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }
        user.setPasswordHash(encoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO userData(User loggedInUser) {
        User user = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));
        return new UserResponseDTO(user);
    }

    @Transactional
    public UserUpdatedResponseDTO editUserData(User loggedInUser, UserEditProfileRequestDTO dto) {
        User user = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));

        if (dto.email() != null && !dto.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            }
            user.setEmail(dto.email().toLowerCase());
        }

        if (dto.name() != null && !dto.name().isBlank()) user.setName(dto.name().toUpperCase());
        if (dto.phone() != null) user.setPhone(formatToE164(dto.phone()));

        userRepository.saveAndFlush(user);
        String newToken = tokenService.generateToken(user);

        return new UserUpdatedResponseDTO(new UserResponseDTO(user), newToken);
    }

    private String formatToE164(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return null;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(rawPhone.trim(), null);
            return phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            return rawPhone.replaceAll("[^0-9+]", "");
        }
    }
}
