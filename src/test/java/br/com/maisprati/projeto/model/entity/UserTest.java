package br.com.maisprati.projeto.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldEncodePasswordWhenPasswordIsValid() {

        // Arrange
        User user = new User();
        String rawPassword = "Password123";
        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        // Act
        user.setEncodedPassword(rawPassword, passwordEncoder);

        // Assert
        assertEquals(
                encodedPassword,
                user.getPassword()
        );

        verify(passwordEncoder)
                .encode(rawPassword);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {

        // Arrange
        User user = new User();

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setEncodedPassword(null, passwordEncoder)
        );

        // Assert
        assertEquals(
                "A senha não pode estar em branco.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(null);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {

        // Arrange
        User user = new User();

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setEncodedPassword("   ", passwordEncoder)
        );

        // Assert
        assertEquals(
                "A senha não pode estar em branco.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode("   ");
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasLessThanSixCharacters() {

        // Arrange
        User user = new User();
        String shortPassword = "12345";

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setEncodedPassword(
                        shortPassword,
                        passwordEncoder
                )
        );

        // Assert
        assertEquals(
                "A senha deve possuir no mínimo 6 caracteres.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(shortPassword);
    }

    @Test
    void shouldAcceptPasswordWithExactlySixCharacters() {

        // Arrange
        User user = new User();
        String rawPassword = "123456";
        String encodedPassword = "encoded-six-character-password";

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        // Act
        user.setEncodedPassword(rawPassword, passwordEncoder);

        // Assert
        assertEquals(
                encodedPassword,
                user.getPassword()
        );

        verify(passwordEncoder)
                .encode(rawPassword);
    }

    @Test
    void shouldReturnEmailAsUsername() {

        // Arrange
        User user = new User();
        user.setEmail("test@email.com");

        // Act
        String username = user.getUsername();

        // Assert
        assertEquals(
                "test@email.com",
                username
        );
    }

    @Test
    void shouldReturnTrueForAccountStatusMethods() {

        // Arrange
        User user = new User();

        // Act + Assert
        assertEquals(
                true,
                user.isAccountNonExpired()
        );

        assertEquals(
                true,
                user.isAccountNonLocked()
        );

        assertEquals(
                true,
                user.isCredentialsNonExpired()
        );
    }

    @Test
    void shouldReturnTrueWhenUserIsActive() {

        // Arrange
        User user = new User();
        user.setActive(true);

        // Act + Assert
        assertEquals(
                true,
                user.isEnabled()
        );
    }

    @Test
    void shouldReturnFalseWhenUserIsInactive() {

        // Arrange
        User user = new User();
        user.setActive(false);

        // Act + Assert
        assertEquals(
                false,
                user.isEnabled()
        );
    }
}
