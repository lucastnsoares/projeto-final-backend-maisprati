package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.model.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String chaveSecreta;



    public String gerarToken(Usuario usuario){
        Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta);
        try {
            return JWT.create()
                    .withIssuer("api-projeto-mais-pra-ti")
                    .withSubject(usuario.getEmail())
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Instant.now().plusSeconds(360))
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token){
        Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta);
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
}
