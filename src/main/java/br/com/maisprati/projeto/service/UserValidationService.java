package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;

    public void validateNewUser(String email, String document) {
        if (userRepository.existsByDocument(document)) {
            throw new IllegalArgumentException("Documento já cadastrado.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já utilizado.");
        }
    }

    public void validateEmailUpdate(String newEmail, String currentEmail) {
        if (newEmail != null && !newEmail.equalsIgnoreCase(currentEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            }
        }
    }

    public void validateDocumentUpdate(String newDocument, String currentDocument) {
        if (newDocument != null && !newDocument.isBlank()) {
            String cleanDoc = newDocument.replaceAll("[.\\-/]", "").trim().toUpperCase();
            if (!cleanDoc.equals(currentDocument)) {
                if (userRepository.existsByDocument(cleanDoc)) {
                    throw new IllegalArgumentException("Documento já cadastrado.");
                }
            }
        }
    }
}
