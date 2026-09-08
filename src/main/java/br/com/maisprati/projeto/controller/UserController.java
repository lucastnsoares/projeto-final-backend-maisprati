package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.UserChangePasswordRequestDTO;
import br.com.maisprati.projeto.dto.request.UserEditProfileRequestDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.UserUpdatedResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuário Logado", description = "Endpoints para gerenciamento do perfil e credenciais do próprio usuário")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Consultar dados da conta", description = "Recupera todas as informações do perfil autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados recuperados com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 401,
                              "error": "Unauthorized",
                              "message": "Token de autenticação ausente ou inválido.",
                              "path": "/api/v1/user"
                            }
                            """)))
    })
    @GetMapping
    public ResponseEntity<UserResponseDTO> userData(
            @AuthenticationPrincipal User loggedInUser
    ) {
        var data = userService.userData(loggedInUser);
        return ResponseEntity.ok(data);
    }

    @Operation(summary = "Alterar senha da conta", description = "Modifica a senha do usuário após verificação da senha atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha fora do padrão",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "A senha atual informada está incorreta.",
                              "path": "/api/v1/user/password"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 401,
                              "error": "Unauthorized",
                              "message": "Token de autenticação ausente ou inválido.",
                              "path": "/api/v1/user/password"
                            }
                            """)))
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid UserChangePasswordRequestDTO dto,
            @AuthenticationPrincipal User loggedInUser){
        userService.changePassword(loggedInUser, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Editar perfil", description = "Atualiza nome, e-mail e telefone do usuário logado e retorna o novo token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserUpdatedResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou e-mail já cadastrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(anyOf = {ValidationErrorResponseDTO.class, ErrorResponseDTO.class}),
                            examples = {
                                    @ExampleObject(name = "Erro de Validação", value = """
                                    {
                                      "timestamp": "2026-08-27T00:00:00Z",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "Erro de validação nos campos enviados.",
                                      "path": "/api/v1/user",
                                      "fieldErrors": [
                                        { "field": "phone", "message": "Número de telefone inválido no padrão internacional." }
                                      ]
                                    }
                                    """),
                                    @ExampleObject(name = "E-mail Duplicado", value = """
                                    {
                                      "timestamp": "2026-08-27T00:00:00Z",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "E-mail já cadastrado.",
                                      "path": "/api/v1/user"
                                    }
                                    """)
                            })),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 401,
                              "error": "Unauthorized",
                              "message": "Token de autenticação ausente ou inválido.",
                              "path": "/api/v1/user"
                            }
                            """)))
    })
    @PatchMapping
    public ResponseEntity<UserUpdatedResponseDTO> editUserData(
            @RequestBody @Valid UserEditProfileRequestDTO dto,
            @AuthenticationPrincipal User loggedInUser
    ) {
        var userUpdated = userService.editUserData(loggedInUser, dto);
        return ResponseEntity.ok(userUpdated);
    }
}
