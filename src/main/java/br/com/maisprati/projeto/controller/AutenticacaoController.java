package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.LoginRequestDTO;
import br.com.maisprati.projeto.dto.response.LoginSucessoResponseDTO;
import br.com.maisprati.projeto.model.entity.Usuario;
import br.com.maisprati.projeto.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Gerenciamento de autenticação")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginSucessoResponseDTO> realizarLogin(@RequestBody LoginRequestDTO dto) {
        var authUser = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var authentication = authenticationManager.authenticate(authUser);
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return  ResponseEntity.ok(new LoginSucessoResponseDTO(tokenJWT));
    }
}
