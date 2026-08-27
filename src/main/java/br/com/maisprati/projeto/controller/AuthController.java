package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.AuthLoginRequestDTO;
import br.com.maisprati.projeto.dto.request.AuthPasswordResetRequestDTO;
import br.com.maisprati.projeto.dto.request.AuthForgotPasswordRequestDTO;
import br.com.maisprati.projeto.dto.response.AuthTokenResponseDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.service.PasswordResetTokenService;
import br.com.maisprati.projeto.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login e recuperação de senha")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna o token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Campos da requisição inválidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ValidationErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "Erro de validação nos campos enviados.",
                              "path": "/api/v1/auth/login",
                              "fieldErrors": [
                                { "field": "email", "message": "O e-mail é obrigatório." },
                                { "field": "password", "message": "A senha é obrigatória." }
                              ]
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou conta inativa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 401,
                              "error": "Unauthorized",
                              "message": "Usuário ou senha inválidos.",
                              "path": "/api/v1/auth/login"
                            }
                            """)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDTO> doLogin(@RequestBody @Valid AuthLoginRequestDTO dto) {
        var authUser = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var authentication = authenticationManager.authenticate(authUser);
        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new AuthTokenResponseDTO(tokenJWT));
    }

    @Operation(summary = "Solicitar redefinição de senha", description = "Dispara envio de e-mail com token caso o endereço exista na base.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitação recebida e processada"),
            @ApiResponse(responseCode = "400", description = "Formato de e-mail inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ValidationErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "Erro de validação nos campos enviados.",
                              "path": "/api/v1/auth/forgot-password",
                              "fieldErrors": [
                                { "field": "email", "message": "Formato de e-mail inválido." }
                              ]
                            }
                            """)))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid AuthForgotPasswordRequestDTO dto) {
        passwordResetTokenService.forgotPassword(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Checar token de recuperação", description = "Verifica se o token recebido por e-mail é válido e ainda não expirou.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "404", description = "Token inválido ou expirado")
    })
    @GetMapping("/forgot-password/check-token")
    public ResponseEntity<Void> checkPasswordResetToken(@RequestParam @Valid String token) {
        if (passwordResetTokenService.checkPasswordResetToken(token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Redefinir senha", description = "Define a nova senha do usuário através do token de redefinição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token expirado, inválido ou senha fora do padrão",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "Token inválido ou expirado.",
                              "path": "/api/v1/auth/forgot-password/reset"
                            }
                            """)))
    })
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid AuthPasswordResetRequestDTO dto) {
        passwordResetTokenService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }

}
