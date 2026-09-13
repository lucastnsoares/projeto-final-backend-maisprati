package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {

        // Arrange
        String email = "user@email.com";

        User user = new User();
        user.setEmail(email);

        org.springframework.test.util.ReflectionTestUtils.setField(
                user,
                "passwordHash",
                "encoded-password"
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails result =
                authService.loadUserByUsername(email);

        // Assert
        assertSame(
                user,
                result
        );

        assertEquals(
                email,
                result.getUsername()
        );

        verify(userRepository)
                .findByEmail(email);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {

        // Arrange
        String email = "missing@email.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.loadUserByUsername(email)
        );

        // Assert
        assertEquals(
                "Usuário não encontrado: " + email,
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail(email);
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenAccountHasNoPassword() {

        // Arrange
        String email = "pending@email.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // Act
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.loadUserByUsername(email)
        );

        // Assert
        assertEquals(
                "Conta pendente de ativação. Defina a senha através do link enviado por e-mail.",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail(email);
    }
}
