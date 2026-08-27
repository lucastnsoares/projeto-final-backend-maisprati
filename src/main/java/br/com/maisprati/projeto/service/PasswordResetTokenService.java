package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.AuthForgotPasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.AuthPasswordResetRequestDTO;
import br.com.maisprati.projeto.model.entity.PasswordResetToken;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.PasswordResetTokenRepository;
import br.com.maisprati.projeto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository  passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Boolean checkPasswordResetToken(String token) {
        String tokenHash = hashToken(token);
        return passwordResetTokenRepository.findByTokenHash(tokenHash)
                .map(tokenData -> !tokenData.isExpired())
                .orElse(false);
    }

    @Transactional
    public void forgotPassword(AuthForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email().toLowerCase()).orElse(null);
        if (user!= null) {
            passwordResetTokenRepository.deleteByUser(user);
            passwordResetTokenRepository.flush();

            String token = UUID.randomUUID().toString();
            String tokenHash = hashToken(token);
            Instant expirationDate = Instant.now().plusSeconds(600);

            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setTokenHash(tokenHash);
            passwordResetToken.setUser(user);
            passwordResetToken.setExpirationDate(expirationDate);

            passwordResetTokenRepository.saveAndFlush(passwordResetToken);
            emailService.sendEmailTest(user.getEmail(), token);
        }
    }

    @Transactional
    public void resetPassword(AuthPasswordResetRequestDTO dto) {
        String tokenHash = hashToken(dto.token());
        PasswordResetToken tokenData = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        if (tokenData.isExpired()) {
            passwordResetTokenRepository.delete(tokenData);
            throw new IllegalArgumentException("Token inválido.");
        }
        User user =  tokenData.getUser();
        user.setEncodedPassword(dto.newPassword(), passwordEncoder);
        userRepository.saveAndFlush(user);
        passwordResetTokenRepository.delete(tokenData);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash do token", e);
        }
    }
}
