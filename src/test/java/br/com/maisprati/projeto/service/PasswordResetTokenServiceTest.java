package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.AuthForgotPasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.AuthPasswordResetRequestDTO;
import br.com.maisprati.projeto.model.entity.PasswordResetToken;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.PasswordResetTokenRepository;
import br.com.maisprati.projeto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

    @Test
    void shouldReturnTrueWhenPasswordResetTokenIsValid() {

        // Arrange
        String rawToken = "valid-token";
        String tokenHash = hashToken(rawToken);

        PasswordResetToken tokenData = new PasswordResetToken();
        tokenData.setTokenHash(tokenHash);
        tokenData.setExpirationDate(
                Instant.now().plusSeconds(600)
        );

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.of(tokenData));

        // Act
        Boolean result =
                passwordResetTokenService.checkPasswordResetToken(rawToken);

        // Assert
        assertTrue(result);

        verify(passwordResetTokenRepository)
                .findByTokenHash(tokenHash);
    }

    @Test
    void shouldReturnFalseWhenPasswordResetTokenIsExpired() {

        // Arrange
        String rawToken = "expired-token";
        String tokenHash = hashToken(rawToken);

        PasswordResetToken tokenData = new PasswordResetToken();
        tokenData.setTokenHash(tokenHash);
        tokenData.setExpirationDate(
                Instant.now().minusSeconds(60)
        );

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.of(tokenData));

        // Act
        Boolean result =
                passwordResetTokenService.checkPasswordResetToken(rawToken);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenPasswordResetTokenDoesNotExist() {

        // Arrange
        String rawToken = "missing-token";
        String tokenHash = hashToken(rawToken);

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.empty());

        // Act
        Boolean result =
                passwordResetTokenService.checkPasswordResetToken(rawToken);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldCreatePasswordResetTokenWhenUserExists() {

        // Arrange
        AuthForgotPasswordRequestDTO request =
                new AuthForgotPasswordRequestDTO(
                        "USER@EMAIL.COM"
                );

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("user@email.com");

        when(userRepository.findByEmail("user@email.com"))
                .thenReturn(Optional.of(user));

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(
                        PasswordResetToken.class
                );

        // Act
        passwordResetTokenService.forgotPassword(request);

        // Assert
        verify(userRepository)
                .findByEmail("user@email.com");

        verify(passwordResetTokenRepository)
                .deleteByUser(user);

        verify(passwordResetTokenRepository)
                .flush();

        verify(passwordResetTokenRepository)
                .saveAndFlush(tokenCaptor.capture());

        PasswordResetToken savedToken =
                tokenCaptor.getValue();

        assertNotNull(savedToken.getTokenHash());
        assertEquals(
                64,
                savedToken.getTokenHash().length()
        );

        assertEquals(
                user,
                savedToken.getUser()
        );

        assertNotNull(
                savedToken.getExpirationDate()
        );

        assertTrue(
                savedToken.getExpirationDate()
                        .isAfter(Instant.now())
        );

        verify(emailService)
                .sendSimpleEmail(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void shouldNotCreatePasswordResetTokenWhenUserDoesNotExist() {

        // Arrange
        AuthForgotPasswordRequestDTO request =
                new AuthForgotPasswordRequestDTO(
                        "missing@email.com"
                );

        when(userRepository.findByEmail("missing@email.com"))
                .thenReturn(Optional.empty());

        // Act
        passwordResetTokenService.forgotPassword(request);

        // Assert
        verify(passwordResetTokenRepository, never())
                .saveAndFlush(any());

        verify(passwordResetTokenRepository, never())
                .deleteByUser(any());

        verify(emailService, never())
                .sendSimpleEmail(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void shouldResetPasswordWhenTokenIsValid() {

        // Arrange
        String rawToken = "valid-token";
        String tokenHash = hashToken(rawToken);

        AuthPasswordResetRequestDTO request =
                new AuthPasswordResetRequestDTO(
                        rawToken,
                        "NewPassword123"
                );

        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        PasswordResetToken tokenData =
                new PasswordResetToken();

        tokenData.setTokenHash(tokenHash);
        tokenData.setUser(user);
        tokenData.setExpirationDate(
                Instant.now().plusSeconds(600)
        );

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.of(tokenData));

        when(passwordEncoder.encode("NewPassword123"))
                .thenReturn("encoded-new-password");

        // Act
        passwordResetTokenService.resetPassword(request);

        // Assert
        assertEquals(
                "encoded-new-password",
                user.getPassword()
        );

        verify(passwordEncoder)
                .encode("NewPassword123");

        verify(userRepository)
                .saveAndFlush(user);

        verify(passwordResetTokenRepository)
                .delete(tokenData);
    }

    @Test
    void shouldThrowExceptionWhenResetTokenDoesNotExist() {

        // Arrange
        String rawToken = "invalid-token";
        String tokenHash = hashToken(rawToken);

        AuthPasswordResetRequestDTO request =
                new AuthPasswordResetRequestDTO(
                        rawToken,
                        "NewPassword123"
                );

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetTokenService
                                .resetPassword(request)
                );

        // Assert
        assertEquals(
                "Token inválido",
                exception.getMessage()
        );

        verify(userRepository, never())
                .saveAndFlush(any());

        verify(passwordResetTokenRepository, never())
                .delete(any());
    }

    @Test
    void shouldDeleteExpiredTokenAndThrowException() {

        // Arrange
        String rawToken = "expired-token";
        String tokenHash = hashToken(rawToken);

        AuthPasswordResetRequestDTO request =
                new AuthPasswordResetRequestDTO(
                        rawToken,
                        "NewPassword123"
                );

        PasswordResetToken tokenData =
                new PasswordResetToken();

        tokenData.setTokenHash(tokenHash);
        tokenData.setExpirationDate(
                Instant.now().minusSeconds(60)
        );

        when(passwordResetTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.of(tokenData));

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> passwordResetTokenService
                                .resetPassword(request)
                );

        // Assert
        assertEquals(
                "Token inválido.",
                exception.getMessage()
        );

        verify(passwordResetTokenRepository)
                .delete(tokenData);

        verify(userRepository, never())
                .saveAndFlush(any());
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }
}
