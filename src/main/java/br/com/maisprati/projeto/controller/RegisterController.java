package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.UserRegisterRequestDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.UserSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.service.RegisterUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@Tag(name = "Cadastro Público", description = "Endpoints de auto-cadastro de novos doadores")
public class RegisterController {
    private final RegisterUserService registerUserService;

    @Operation(summary = "Registrar doador", description = "Cria uma nova conta de usuário público com o perfil DOADOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doador cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserSummaryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos campos enviados ou duplicidade de e-mail/documento",
                    content = @Content(schema = @Schema(anyOf = {ValidationErrorResponseDTO.class, ErrorResponseDTO.class})))
    })
    @PostMapping
    public ResponseEntity<UserSummaryResponseDTO> registerUser(@RequestBody @Valid UserRegisterRequestDTO dto) {
        var userCreatedDTO = registerUserService.registerUserPublic(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreatedDTO);
    }

}
