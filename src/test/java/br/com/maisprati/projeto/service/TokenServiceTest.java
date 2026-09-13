package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceTest {

    private TokenService tokenService;

    private static final String SECRET_KEY =
            "test-secret-key-for-jwt-tests";

    private static final String ISSUER =
            "test-issuer";

    private static final Integer EXPIRATION_SECONDS =
            3600;

    @BeforeEach
    void setUp() {

        tokenService = new TokenService();

        ReflectionTestUtils.setField(
                tokenService,
                "secretKey",
                SECRET_KEY
        );

        ReflectionTestUtils.setField(
                tokenService,
                "issuer",
                ISSUER
        );

        ReflectionTestUtils.setField(
                tokenService,
                "expirationSeconds",
                EXPIRATION_SECONDS
        );
    }

    @Test
    void shouldGenerateValidToken() {

        // Arrange
        User user = createUser();

        // Act
        String token =
                tokenService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertTrue(!token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithCorrectUserData() {

        // Arrange
        User user = createUser();

        // Act
        String token =
                tokenService.generateToken(user);

        User tokenUser =
                tokenService.getUserFromToken(token);

        // Assert
        assertNotNull(tokenUser);

        assertEquals(
                1L,
                tokenUser.getId()
        );

        assertEquals(
                "user@email.com",
                tokenUser.getEmail()
        );

        assertEquals(
                "Test User",
                tokenUser.getName()
        );

        assertEquals(
                Set.of(
                        Role.DOADOR,
                        Role.ADMIN
                ),
                tokenUser.getRole()
        );

        assertTrue(
                tokenUser.getActive()
        );
    }

    @Test
    void shouldReturnNullWhenTokenIsInvalid() {

        // Arrange
        String invalidToken =
                "invalid.jwt.token";

        // Act
        User result =
                tokenService.getUserFromToken(invalidToken);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenTokenHasWrongIssuer() {

        // Arrange
        Algorithm algorithm =
                Algorithm.HMAC256(SECRET_KEY);

        String token = JWT.create()
                .withIssuer("wrong-issuer")
                .withSubject("user@email.com")
                .withClaim("id", 1L)
                .withClaim("name", "Test User")
                .withArrayClaim(
                        "roles",
                        new String[]{"DOADOR"}
                )
                .withIssuedAt(
                        Date.from(Instant.now())
                )
                .withExpiresAt(
                        Date.from(
                                Instant.now()
                                        .plusSeconds(3600)
                        )
                )
                .sign(algorithm);

        // Act
        User result =
                tokenService.getUserFromToken(token);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenTokenDoesNotContainRoles() {

        // Arrange
        Algorithm algorithm =
                Algorithm.HMAC256(SECRET_KEY);

        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject("user@email.com")
                .withClaim("id", 1L)
                .withClaim("name", "Test User")
                .withIssuedAt(
                        Date.from(Instant.now())
                )
                .withExpiresAt(
                        Date.from(
                                Instant.now()
                                        .plusSeconds(3600)
                        )
                )
                .sign(algorithm);

        // Act
        User result =
                tokenService.getUserFromToken(token);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenTokenIsExpired() {

        // Arrange
        Algorithm algorithm =
                Algorithm.HMAC256(SECRET_KEY);

        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject("user@email.com")
                .withClaim("id", 1L)
                .withClaim("name", "Test User")
                .withArrayClaim(
                        "roles",
                        new String[]{"DOADOR"}
                )
                .withIssuedAt(
                        Date.from(
                                Instant.now()
                                        .minusSeconds(7200)
                        )
                )
                .withExpiresAt(
                        Date.from(
                                Instant.now()
                                        .minusSeconds(3600)
                        )
                )
                .sign(algorithm);

        // Act
        User result =
                tokenService.getUserFromToken(token);

        // Assert
        assertNull(result);
    }

    private User createUser() {

        User user = new User();

        user.setId(1L);
        user.setName("Test User");
        user.setEmail("user@email.com");

        user.setRole(
                Set.of(
                        Role.DOADOR,
                        Role.ADMIN
                )
        );

        user.setActive(true);

        return user;
    }
}