package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.ClothTypeRequestDTO;
import br.com.maisprati.projeto.dto.request.ClothTypeUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.ClothTypeResponseDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.service.ClothTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cloth-types")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administração de Tipos de Tecido", description = "Gerenciamento e controle de tipos de tecido pelo Administrador")
public class ClothTypeController {
    private final ClothTypeService clothTypeService;

    @GetMapping
    @Operation(summary = "Listar tipos de tecido", description = "Retorna uma lista paginada de tipos de tecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipos de tecido listados com sucesso.",
                    content = @Content(schema = @Schema(implementation = ClothTypeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Page<ClothTypeResponseDTO>> getClothTypes(@ParameterObject @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        var page = clothTypeService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de tecido por ID", description = "Retorna os dados de um tipo de tecido específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de tecido encontrado com sucesso.",
                    content = @Content(schema = @Schema(implementation = ClothTypeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de tecido não localizado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ClothTypeResponseDTO> getClothTypeByid(@PathVariable Long id) {
        return ResponseEntity.ok().body(clothTypeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar tipo de tecido", description = "Cadastra um novo tipo de tecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de tecido criado com sucesso.",
                    content = @Content(schema = @Schema(implementation = ClothTypeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou tipo de tecido já existente.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ClothTypeResponseDTO> createClothType(@RequestBody @Valid ClothTypeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clothTypeService.createClothType(dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar tipo de tecido por ID", description = "Atualiza os dados de um tipo de tecido específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de tecido atualizado com sucesso.",
                    content = @Content(schema = @Schema(implementation = ClothTypeResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou tipo de tecido já existente.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de tecido não localizado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ClothTypeResponseDTO> updateClothType(
            @PathVariable Long id,
            @RequestBody @Valid ClothTypeUpdateRequestDTO dto) {
        return ResponseEntity.ok(clothTypeService.updateClothType(id, dto));
    }

}
