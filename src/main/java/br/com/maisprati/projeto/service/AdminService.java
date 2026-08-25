package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO updateUser(UserUpdateRequestDTO dto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));

        if (dto.document() != null && !dto.document().equals(user.getDocument())) {
            if (userRepository.existsByDocument(dto.document())) {
                throw new IllegalArgumentException("Documento já cadastrado.");
            }
            user.setDocument(dto.document());
        }

        if (dto.email() != null && !dto.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            }
            user.setEmail(dto.email().toLowerCase());
        }

        if (dto.name() != null && !dto.name().isBlank()) user.setName(dto.name().toUpperCase());
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.roles() != null && !dto.roles().isEmpty()) user.setRole(dto.roles());
        if (dto.isActive() != null) user.setActive(dto.isActive());

        userRepository.saveAndFlush(user);
        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));
        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseDTO::new);
    }
}
