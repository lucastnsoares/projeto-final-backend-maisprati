package br.com.maisprati.projeto.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        when(request.getRequestURI())
                .thenReturn("/test");
    }

    @Test
    void shouldHandleAuthenticationException() {
        var response =
                handler.handleAutenticacaoException(request);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleAuthorizationDeniedException() {
        var response =
                handler.handleAuthorizationDeniedException(request);

        assertEquals(403, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        var response =
                handler.handleNoResourceFoundException(request);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleInvalidRequest() {
        var response =
                handler.handleInvalidRequest(request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException exception =
                new IllegalArgumentException("Invalid data");

        var response =
                handler.handleIllegalArgumentException(
                        exception,
                        request
                );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleGenericException() {
        Exception exception =
                new RuntimeException("Unexpected error");

        var response =
                handler.handleGenericException(
                        exception,
                        request
                );

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}