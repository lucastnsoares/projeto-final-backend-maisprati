package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.AuthForgotPasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.UserCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.UserRegisterRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.UserSummaryResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    void shouldRegisterPublicUserSuccessfully() {

        // Arrange
        UserRegisterRequestDTO request = new UserRegisterRequestDTO(
                "Bruna Santiago",
                "123.456.789-00",
                "TEST@EMAIL.COM",
                "Password123",
                "+5551999999999"
        );

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encoded-password");

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserSummaryResponseDTO response =
                registerUserService.registerUserPublic(request);

        // Assert
        assertNotNull(response);

        verify(userValidationService)
                .validateNewUser(
                        "TEST@EMAIL.COM",
                        "123.456.789-00"
                );

        verify(passwordEncoder)
                .encode("Password123");

        verify(userRepository)
                .saveAndFlush(any(User.class));
    }

    @Test
    void shouldAssignDonorRoleToPublicUser() {

        // Arrange
        UserRegisterRequestDTO request = new UserRegisterRequestDTO(
                "Bruna Santiago",
                "123.456.789-00",
                "test@email.com",
                "Password123",
                "+5551999999999"
        );

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encoded-password");

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        registerUserService.registerUserPublic(request);

        // Assert
        verify(userRepository)
                .saveAndFlush(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                Set.of(Role.DOADOR),
                savedUser.getRole()
        );
    }

    @Test
    void shouldNormalizePublicUserDataBeforeSaving() {

        // Arrange
        UserRegisterRequestDTO request = new UserRegisterRequestDTO(
                "Bruna Santiago",
                "123.456.789-00",
                "TEST@EMAIL.COM",
                "Password123",
                "+55 51 99999-9999"
        );

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encoded-password");

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        registerUserService.registerUserPublic(request);

        // Assert
        verify(userRepository)
                .saveAndFlush(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "BRUNA SANTIAGO",
                savedUser.getName()
        );

        assertEquals(
                "12345678900",
                savedUser.getDocument()
        );

        assertEquals(
                "test@email.com",
                savedUser.getEmail()
        );

        assertEquals(
                "+5551999999999",
                savedUser.getPhone()
        );

        assertEquals(
                "encoded-password",
                savedUser.getPassword()
        );
    }

    @Test
    void shouldEncodePasswordWhenRegisteringPublicUser() {

        // Arrange
        UserRegisterRequestDTO request = new UserRegisterRequestDTO(
                "Bruna Santiago",
                "12345678900",
                "test@email.com",
                "Password123",
                "+5551999999999"
        );

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encoded-password");

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        registerUserService.registerUserPublic(request);

        // Assert
        verify(passwordEncoder)
                .encode("Password123");

        verify(userRepository)
                .saveAndFlush(userCaptor.capture());

        assertEquals(
                "encoded-password",
                userCaptor.getValue().getPassword()
        );
    }

    @Test
    void shouldNotSavePublicUserWhenValidationFails() {

        // Arrange
        UserRegisterRequestDTO request = new UserRegisterRequestDTO(
                "Bruna Santiago",
                "12345678900",
                "test@email.com",
                "Password123",
                "+5551999999999"
        );

        doThrow(new IllegalArgumentException("User already exists"))
                .when(userValidationService)
                .validateNewUser(
                        request.email(),
                        request.document()
                );

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerUserService.registerUserPublic(request)
        );

        assertEquals(
                "User already exists",
                exception.getMessage()
        );

        verify(userRepository, never())
                .saveAndFlush(any(User.class));

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void shouldRegisterAdminUserSuccessfully() {

        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "Maria Oliveira",
                "529.982.247-25",
                "MARIA@EMAIL.COM",
                "+5551998765432",
                Set.of(
                        Role.ADMIN,
                        Role.PONTO_COLETA_GERENTE
                )
        );

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponseDTO response =
                registerUserService.registerUserAdmin(request);

        // Assert
        assertNotNull(response);

        verify(userValidationService)
                .validateNewUser(
                        request.email(),
                        request.document()
                );

        verify(userRepository)
                .saveAndFlush(any(User.class));

        verify(passwordResetTokenService)
                .forgotPassword(
                        any(AuthForgotPasswordRequestDTO.class)
                );
    }

    @Test
    void shouldPreserveRolesWhenRegisteringAdminUser() {

        // Arrange
        Set<Role> roles = Set.of(
                Role.ADMIN,
                Role.PONTO_COLETA_GERENTE
        );

        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "Maria Oliveira",
                "529.982.247-25",
                "maria@email.com",
                "+5551998765432",
                roles
        );

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        registerUserService.registerUserAdmin(request);

        // Assert
        verify(userRepository)
                .saveAndFlush(userCaptor.capture());

        assertEquals(
                roles,
                userCaptor.getValue().getRole()
        );
    }

    @Test
    void shouldNormalizeAdminUserDataBeforeSaving() {

        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "Maria Oliveira",
                "529.982.247-25",
                "MARIA@EMAIL.COM",
                "+55 51 99876-5432",
                Set.of(Role.ADMIN)
        );

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        registerUserService.registerUserAdmin(request);

        // Assert
        verify(userRepository)
                .saveAndFlush(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "MARIA OLIVEIRA",
                savedUser.getName()
        );

        assertEquals(
                "52998224725",
                savedUser.getDocument()
        );

        assertEquals(
                "maria@email.com",
                savedUser.getEmail()
        );

        assertEquals(
                "+5551998765432",
                savedUser.getPhone()
        );

        assertEquals(
                Set.of(Role.ADMIN),
                savedUser.getRole()
        );
    }

    @Test
    void shouldTriggerPasswordResetFlowAfterAdminRegistration() {

        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "Maria Oliveira",
                "52998224725",
                "maria@email.com",
                "+5551998765432",
                Set.of(Role.ADMIN)
        );

        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        registerUserService.registerUserAdmin(request);

        // Assert
        verify(passwordResetTokenService)
                .forgotPassword(
                        argThat(dto ->
                                dto != null
                                        && dto.email().equals(
                                        "maria@email.com"
                                )
                        )
                );
    }

    @Test
    void shouldNotSaveAdminUserWhenValidationFails() {

        // Arrange
        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "Maria Oliveira",
                "52998224725",
                "maria@email.com",
                "+5551998765432",
                Set.of(Role.ADMIN)
        );

        doThrow(new IllegalArgumentException("User already exists"))
                .when(userValidationService)
                .validateNewUser(
                        request.email(),
                        request.document()
                );

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerUserService.registerUserAdmin(request)
        );

        assertEquals(
                "User already exists",
                exception.getMessage()
        );

        verify(userRepository, never())
                .saveAndFlush(any(User.class));

        verify(passwordResetTokenService, never())
                .forgotPassword(any());
    }
}
