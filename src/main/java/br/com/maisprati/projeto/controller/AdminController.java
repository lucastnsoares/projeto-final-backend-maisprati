package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.UserCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.UserUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.service.AdminService;
import br.com.maisprati.projeto.service.RegisterUserService;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administração de Usuários", description = "Gerenciamento e controle de acessos de contas pelo Administrador")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final AdminService adminService;
    private final RegisterUserService registerUserService;

    @Operation(summary = "Listar todos os usuários", description = "Retorna lista paginada de contas cadastradas com suporte a ordenação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada retornada"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 401,
                              "error": "Unauthorized",
                              "message": "Token de autenticação ausente ou inválido.",
                              "path": "/api/v1/admin/users"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 403,
                              "error": "Forbidden",
                              "message": "Você não possui autorização para acessar este recurso.",
                              "path": "/api/v1/admin/users"
                            }
                            """)))
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> listUsers(
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {
        var page = adminService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Criar usuário administrativo", description = "Cadastra usuário com roles definidas e envia e-mail com instruções para ativação/senha.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou e-mail/documento já cadastrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(anyOf = {ValidationErrorResponseDTO.class, ErrorResponseDTO.class}),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "Documento já cadastrado.",
                              "path": "/api/v1/admin/users"
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
                              "path": "/api/v1/admin/users"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 403,
                              "error": "Forbidden",
                              "message": "Você não possui autorização para acessar este recurso.",
                              "path": "/api/v1/admin/users"
                            }
                            """)))
    })
    @PostMapping ("/users")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid UserCreateRequestDTO dto
            ) {
        var newUser = registerUserService.registerUserAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Consulta informações completas de um usuário pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário localizado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 404,
                              "error": "Not Found",
                              "message": "Usuário não encontrado no banco de dados.",
                              "path": "/api/v1/admin/users/99"
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
                              "path": "/api/v1/admin/users/99"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 403,
                              "error": "Forbidden",
                              "message": "Você não possui autorização para acessar este recurso.",
                              "path": "/api/v1/admin/users/99"
                            }
                            """)))
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(
            @PathVariable Long id
    ) {
        var user = adminService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "Atualizar usuário por ID", description = "Altera campos cadastrais, perfis de acesso ou status ativo da conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou conflito de dados",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(anyOf = {ValidationErrorResponseDTO.class, ErrorResponseDTO.class}),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "E-mail já cadastrado.",
                              "path": "/api/v1/admin/users/1"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 404,
                              "error": "Not Found",
                              "message": "Usuário não encontrado no banco de dados.",
                              "path": "/api/v1/admin/users/99"
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
                              "path": "/api/v1/admin/users/1"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-08-27T00:00:00Z",
                              "status": 403,
                              "error": "Forbidden",
                              "message": "Você não possui autorização para acessar este recurso.",
                              "path": "/api/v1/admin/users/1"
                            }
                            """)))
    })
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDTO dto
    ) {
        var userUpdated = adminService.updateUser(dto, id);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdated);
    }
}
