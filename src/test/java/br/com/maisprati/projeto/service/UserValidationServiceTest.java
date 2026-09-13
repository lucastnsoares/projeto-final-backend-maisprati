package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidationService userValidationService;

    @Test
    void shouldValidateNewUserWhenDocumentAndEmailDoNotExist() {

        // Arrange
        String email = "test@email.com";
        String document = "12345678900";

        when(userRepository.existsByDocument(document))
                .thenReturn(false);

        when(userRepository.existsByEmail(email))
                .thenReturn(false);

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateNewUser(email, document)
        );

        verify(userRepository).existsByDocument(document);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenDocumentAlreadyExists() {

        // Arrange
        String email = "test@email.com";
        String document = "12345678900";

        when(userRepository.existsByDocument(document))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidationService.validateNewUser(email, document)
        );

        // Assert
        assertEquals(
                "Documento já cadastrado.",
                exception.getMessage()
        );

        verify(userRepository).existsByDocument(document);
        verify(userRepository, never()).existsByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        String email = "test@email.com";
        String document = "12345678900";

        when(userRepository.existsByDocument(document))
                .thenReturn(false);

        when(userRepository.existsByEmail(email))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidationService.validateNewUser(email, document)
        );

        // Assert
        assertEquals(
                "E-mail já utilizado.",
                exception.getMessage()
        );

        verify(userRepository).existsByDocument(document);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void shouldValidateEmailUpdateWhenNewEmailIsAvailable() {

        // Arrange
        String currentEmail = "current@email.com";
        String newEmail = "new@email.com";

        when(userRepository.existsByEmail(newEmail))
                .thenReturn(false);

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateEmailUpdate(newEmail, currentEmail)
        );

        verify(userRepository).existsByEmail(newEmail);
    }

    @Test
    void shouldThrowExceptionWhenUpdatedEmailAlreadyExists() {

        // Arrange
        String currentEmail = "current@email.com";
        String newEmail = "existing@email.com";

        when(userRepository.existsByEmail(newEmail))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidationService.validateEmailUpdate(newEmail, currentEmail)
        );

        // Assert
        assertEquals(
                "E-mail já cadastrado.",
                exception.getMessage()
        );

        verify(userRepository).existsByEmail(newEmail);
    }

    @Test
    void shouldNotValidateEmailWhenNewEmailIsSameAsCurrentEmail() {

        // Arrange
        String currentEmail = "test@email.com";
        String newEmail = "TEST@email.com";

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateEmailUpdate(newEmail, currentEmail)
        );

        verify(userRepository, never()).existsByEmail(newEmail);
    }

    @Test
    void shouldNotValidateEmailWhenNewEmailIsNull() {

        // Arrange
        String currentEmail = "current@email.com";

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateEmailUpdate(null, currentEmail)
        );

        verify(userRepository, never()).existsByEmail(null);
    }

    @Test
    void shouldValidateDocumentUpdateWhenDocumentIsAvailable() {

        // Arrange
        String currentDocument = "12345678900";
        String newDocument = "987.654.321-00";
        String cleanedDocument = "98765432100";

        when(userRepository.existsByDocument(cleanedDocument))
                .thenReturn(false);

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateDocumentUpdate(
                        newDocument,
                        currentDocument
                )
        );

        verify(userRepository).existsByDocument(cleanedDocument);
    }

    @Test
    void shouldThrowExceptionWhenUpdatedDocumentAlreadyExists() {

        // Arrange
        String currentDocument = "12345678900";
        String newDocument = "987.654.321-00";
        String cleanedDocument = "98765432100";

        when(userRepository.existsByDocument(cleanedDocument))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidationService.validateDocumentUpdate(
                        newDocument,
                        currentDocument
                )
        );

        // Assert
        assertEquals(
                "Documento já cadastrado.",
                exception.getMessage()
        );

        verify(userRepository).existsByDocument(cleanedDocument);
    }

    @Test
    void shouldNotValidateDocumentWhenNewDocumentIsSameAsCurrentDocument() {

        // Arrange
        String currentDocument = "12345678900";
        String newDocument = "123.456.789-00";

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateDocumentUpdate(
                        newDocument,
                        currentDocument
                )
        );

        verify(userRepository, never())
                .existsByDocument("12345678900");
    }

    @Test
    void shouldNotValidateDocumentWhenNewDocumentIsNull() {

        // Arrange
        String currentDocument = "12345678900";

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateDocumentUpdate(
                        null,
                        currentDocument
                )
        );

        verify(userRepository, never())
                .existsByDocument(null);
    }

    @Test
    void shouldNotValidateDocumentWhenNewDocumentIsBlank() {

        // Arrange
        String currentDocument = "12345678900";

        // Act + Assert
        assertDoesNotThrow(() ->
                userValidationService.validateDocumentUpdate(
                        "   ",
                        currentDocument
                )
        );

        verify(userRepository, never())
                .existsByDocument("   ");
    }
}