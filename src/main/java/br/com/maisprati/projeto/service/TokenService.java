package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secretKey;

    public String generateToken(User user){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        try {
            List<String> roles = user.getRole().stream()
                    .map(Role::name)
                    .toList();

            return JWT.create()
                    .withIssuer("api-projeto-mais-pra-ti")
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId())
                    .withClaim("name", user.getName())
                    .withClaim("roles", roles)
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Instant.now().plusSeconds(3600))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token){
        Algorithm algoritmo = Algorithm.HMAC256(secretKey);
        try{
            return JWT.require(algoritmo)
                    .withIssuer("api-projeto-mais-pra-ti")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    public User getUserFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        try {
            var verifier = JWT.require(algorithm)
                    .withIssuer("api-projeto-mais-pra-ti")
                    .build()
                    .verify(token);

            String email = verifier.getSubject();
            Long id = verifier.getClaim("id").asLong();
            String name = verifier.getClaim("name").asString();
            List<String> rolesStr = verifier.getClaim("roles").asList(String.class);

            if (email == null || rolesStr == null) return null;

            Set<Role> roles = rolesStr.stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());

            User user = new User();
            user.setId(id);
            user.setEmail(email);
            user.setName(name);
            user.setRole(roles);
            user.setActive(true);

            return user;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
