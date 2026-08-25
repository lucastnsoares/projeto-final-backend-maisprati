package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserChangePasswordRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Transactional
    public void changePassword(User loggedInUser, UserChangePasswordRequestDTO dto){
        if(!encoder.matches(dto.currentPassword(), loggedInUser.getPasswordHash())){
            throw new IllegalArgumentException("A senha atual informada está incorreta");
        }
        loggedInUser.setPasswordHash(encoder.encode(dto.newPassword()));
        userRepository.save(loggedInUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO userData(User loggedInUser) {
        User user = userRepository.findByEmail(loggedInUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));
        return new UserResponseDTO(user);
    }
}
