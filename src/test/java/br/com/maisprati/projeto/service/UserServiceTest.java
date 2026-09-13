package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserChangePasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.UserEditProfileRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.UserUpdatedResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldChangePasswordSuccessfully() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User databaseUser = createUser();

        ReflectionTestUtils.setField(
                databaseUser,
                "passwordHash",
                "current-encoded-password"
        );

        UserChangePasswordRequestDTO request =
                new UserChangePasswordRequestDTO(
                        "CurrentPassword123",
                        "NewPassword123"
                );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(databaseUser));

        when(
                encoder.matches(
                        "CurrentPassword123",
                        "current-encoded-password"
                )
        ).thenReturn(true);

        when(encoder.encode("NewPassword123"))
                .thenReturn("new-encoded-password");

        // Act
        userService.changePassword(
                loggedInUser,
                request
        );

        // Assert
        assertEquals(
                "new-encoded-password",
                databaseUser.getPassword()
        );

        verify(encoder)
                .matches(
                        "CurrentPassword123",
                        "current-encoded-password"
                );

        verify(encoder)
                .encode("NewPassword123");

        verify(userRepository)
                .save(databaseUser);
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User databaseUser = createUser();

        ReflectionTestUtils.setField(
                databaseUser,
                "passwordHash",
                "current-encoded-password"
        );

        UserChangePasswordRequestDTO request =
                new UserChangePasswordRequestDTO(
                        "WrongPassword",
                        "NewPassword123"
                );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(databaseUser));

        when(
                encoder.matches(
                        "WrongPassword",
                        "current-encoded-password"
                )
        ).thenReturn(false);

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.changePassword(
                                loggedInUser,
                                request
                        )
                );

        // Assert
        assertEquals(
                "A senha atual informada está incorreta.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenChangingPasswordForMissingUser() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(99L);

        UserChangePasswordRequestDTO request =
                new UserChangePasswordRequestDTO(
                        "CurrentPassword123",
                        "NewPassword123"
                );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.changePassword(
                                loggedInUser,
                                request
                        )
                );

        // Assert
        assertEquals(
                "Usuário não encontrado no banco de dados.",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnUserDataSuccessfully() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User databaseUser = createUser();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(databaseUser));

        // Act
        UserResponseDTO response =
                userService.userData(loggedInUser);

        // Assert
        assertNotNull(response);

        verify(userRepository)
                .findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserDataDoesNotExist() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(99L);

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.userData(
                                loggedInUser
                        )
                );

        // Assert
        assertEquals(
                "Usuário não encontrado no banco de dados.",
                exception.getMessage()
        );
    }

    @Test
    void shouldEditUserDataSuccessfully() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User databaseUser = createUser();

        UserEditProfileRequestDTO request =
                new UserEditProfileRequestDTO(
                        "Updated User",
                        "NEW@EMAIL.COM",
                        "+55 51 99999-9999"
                );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(databaseUser));

        when(tokenService.generateToken(databaseUser))
                .thenReturn("new-jwt-token");

        // Act
        UserUpdatedResponseDTO response =
                userService.editUserData(
                        loggedInUser,
                        request
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                "UPDATED USER",
                databaseUser.getName()
        );

        assertEquals(
                "new@email.com",
                databaseUser.getEmail()
        );

        assertEquals(
                "+5551999999999",
                databaseUser.getPhone()
        );

        verify(userValidationService)
                .validateEmailUpdate(
                        "NEW@EMAIL.COM",
                        "user@email.com"
                );

        verify(userRepository)
                .saveAndFlush(databaseUser);

        verify(tokenService)
                .generateToken(databaseUser);
    }

    @Test
    void shouldEditUserDataWithoutOptionalFields() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User databaseUser = createUser();

        UserEditProfileRequestDTO request =
                new UserEditProfileRequestDTO(
                        null,
                        null,
                        null
                );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(databaseUser));

        when(tokenService.generateToken(databaseUser))
                .thenReturn("new-jwt-token");

        // Act
        UserUpdatedResponseDTO response =
                userService.editUserData(
                        loggedInUser,
                        request
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                "Test User",
                databaseUser.getName()
        );

        assertEquals(
                "user@email.com",
                databaseUser.getEmail()
        );

        assertEquals(
                "+5551999999999",
                databaseUser.getPhone()
        );

        verify(userValidationService, never())
                .validateEmailUpdate(
                        any(),
                        any()
                );

        verify(userRepository)
                .saveAndFlush(databaseUser);

        verify(tokenService)
                .generateToken(databaseUser);
    }

    @Test
    void shouldThrowExceptionWhenEditingMissingUser() {

        // Arrange
        User loggedInUser = new User();
        loggedInUser.setId(99L);

        UserEditProfileRequestDTO request =
                new UserEditProfileRequestDTO(
                        "Updated User",
                        "updated@email.com",
                        "+5551999999999"
                );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.editUserData(
                                loggedInUser,
                                request
                        )
                );

        // Assert
        assertEquals(
                "Usuário não encontrado no banco de dados.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .saveAndFlush(any());

        verify(tokenService, never())
                .generateToken(any());
    }

    private User createUser() {

        User user = new User();

        user.setId(1L);
        user.setName("Test User");
        user.setDocument("12345678900");
        user.setEmail("user@email.com");
        user.setPhone("+5551999999999");
        user.setRole(Set.of(Role.DOADOR));
        user.setActive(true);

        return user;
    }
}