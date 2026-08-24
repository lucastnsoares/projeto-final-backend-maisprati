package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.AlteracaoDeDadosRequestDTO;
import br.com.maisprati.projeto.dto.request.AlterarSenhaRequestDTO;
import br.com.maisprati.projeto.dto.response.DadosUsuarioLogadoResponseDTO;
import br.com.maisprati.projeto.model.entity.Usuario;
import br.com.maisprati.projeto.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Gerenciamento de dados do Usuário")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<DadosUsuarioLogadoResponseDTO> dadosUsuarioLogado(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var dados = usuarioService.dados(usuarioLogado);
        return ResponseEntity.ok(dados);
    }

    @PatchMapping("/dados")
    public ResponseEntity<Void> alterarDados(
            @RequestBody AlteracaoDeDadosRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
            ){
        usuarioService.alterarDados(usuarioLogado, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> alterarSenha(
            @RequestBody AlterarSenhaRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){
        usuarioService.alterarSenha(usuarioLogado, dto);
        return ResponseEntity.noContent().build();
    }
}
