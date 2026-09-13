package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.UserUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldUpdateUserSuccessfully() {

        // Arrange
        Long userId = 1L;

        User user = createUser();

        UserUpdateRequestDTO request =
                new UserUpdateRequestDTO(
                        "Updated User",
                        "987.654.321-00",
                        "NEW@EMAIL.COM",
                        "+55 51 98888-7777",
                        Set.of(
                                Role.ADMIN,
                                Role.DOADOR
                        ),
                        false
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        UserResponseDTO response =
                adminService.updateUser(
                        request,
                        userId
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                "UPDATED USER",
                user.getName()
        );

        assertEquals(
                "98765432100",
                user.getDocument()
        );

        assertEquals(
                "new@email.com",
                user.getEmail()
        );

        assertEquals(
                "+5551988887777",
                user.getPhone()
        );

        assertEquals(
                Set.of(
                        Role.ADMIN,
                        Role.DOADOR
                ),
                user.getRole()
        );

        assertEquals(
                false,
                user.getActive()
        );

        verify(userValidationService)
                .validateDocumentUpdate(
                        "98765432100",
                        "12345678900"
                );

        verify(userValidationService)
                .validateEmailUpdate(
                        "NEW@EMAIL.COM",
                        "user@email.com"
                );

        verify(userRepository)
                .saveAndFlush(user);
    }

    @Test
    void shouldKeepExistingDataWhenUpdateFieldsAreNull() {

        // Arrange
        Long userId = 1L;

        User user = createUser();

        UserUpdateRequestDTO request =
                new UserUpdateRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        UserResponseDTO response =
                adminService.updateUser(
                        request,
                        userId
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                "Test User",
                user.getName()
        );

        assertEquals(
                "12345678900",
                user.getDocument()
        );

        assertEquals(
                "user@email.com",
                user.getEmail()
        );

        assertEquals(
                "+5551999999999",
                user.getPhone()
        );

        assertEquals(
                Set.of(Role.DOADOR),
                user.getRole()
        );

        assertEquals(
                true,
                user.getActive()
        );

        verify(userValidationService, never())
                .validateDocumentUpdate(
                        any(),
                        any()
                );

        verify(userValidationService, never())
                .validateEmailUpdate(
                        any(),
                        any()
                );

        verify(userRepository)
                .saveAndFlush(user);
    }

    @Test
    void shouldIgnoreBlankNameAndBlankDocument() {

        // Arrange
        Long userId = 1L;

        User user = createUser();

        UserUpdateRequestDTO request =
                new UserUpdateRequestDTO(
                        "   ",
                        "   ",
                        null,
                        null,
                        Set.of(),
                        null
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        adminService.updateUser(
                request,
                userId
        );

        // Assert
        assertEquals(
                "Test User",
                user.getName()
        );

        assertEquals(
                "12345678900",
                user.getDocument()
        );

        assertEquals(
                Set.of(Role.DOADOR),
                user.getRole()
        );

        verify(userValidationService, never())
                .validateDocumentUpdate(
                        any(),
                        any()
                );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingUser() {

        // Arrange
        Long userId = 99L;

        UserUpdateRequestDTO request =
                new UserUpdateRequestDTO(
                        "Updated User",
                        null,
                        null,
                        null,
                        null,
                        null
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adminService.updateUser(
                                request,
                                userId
                        )
                );

        // Assert
        assertEquals(
                "Usuário não encontrado no banco de dados.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .saveAndFlush(any());
    }

    @Test
    void shouldFindUserByIdSuccessfully() {

        // Arrange
        Long userId = 1L;

        User user = createUser();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        UserResponseDTO response =
                adminService.findById(userId);

        // Assert
        assertNotNull(response);

        verify(userRepository)
                .findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserByIdDoesNotExist() {

        // Arrange
        Long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adminService.findById(
                                userId
                        )
                );

        // Assert
        assertEquals(
                "Usuário não encontrado no banco de dados.",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnAllUsersAsPage() {

        // Arrange
        User firstUser = createUser();

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setName("Second User");
        secondUser.setDocument("98765432100");
        secondUser.setEmail("second@email.com");
        secondUser.setPhone("+5551988887777");
        secondUser.setRole(Set.of(Role.ADMIN));
        secondUser.setActive(true);

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<User> userPage =
                new PageImpl<>(
                        List.of(
                                firstUser,
                                secondUser
                        ),
                        pageable,
                        2
                );

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        // Act
        Page<UserResponseDTO> result =
                adminService.findAll(pageable);

        // Assert
        assertNotNull(result);

        assertEquals(
                2,
                result.getTotalElements()
        );

        assertEquals(
                2,
                result.getContent().size()
        );

        verify(userRepository)
                .findAll(pageable);
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
