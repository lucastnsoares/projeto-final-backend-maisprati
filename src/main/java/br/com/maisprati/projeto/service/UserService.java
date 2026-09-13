package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserChangePasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.UserEditProfileRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.UserUpdatedResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.UserRepository;
import br.com.maisprati.projeto.util.PhoneUtils;
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
    private final UserValidationService userValidationService;

    @Transactional
    public void changePassword(User loggedInUser, UserChangePasswordRequestDTO dto){
        User user = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));
        if (!encoder.matches(dto.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }
        user.setEncodedPassword(dto.newPassword(), encoder);
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

        if (dto.email() != null) {
            userValidationService.validateEmailUpdate(dto.email(), user.getEmail());
            user.setEmail(dto.email().toLowerCase());
        }
        if (dto.name() != null && !dto.name().isBlank()) user.setName(dto.name().toUpperCase());
        if (dto.phone() != null) user.setPhone(PhoneUtils.formatToE164(dto.phone()));

        userRepository.saveAndFlush(user);
        String newToken = tokenService.generateToken(user);

        return new UserUpdatedResponseDTO(new UserResponseDTO(user), newToken);
    }
}
