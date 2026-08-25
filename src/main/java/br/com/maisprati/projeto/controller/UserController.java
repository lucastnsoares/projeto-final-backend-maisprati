package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.ChangePasswordRequestDTO;
import br.com.maisprati.projeto.dto.response.UserDataResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Gerenciamento de dados do Usuário logado")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDataResponseDTO> userData(
            @AuthenticationPrincipal User loggedInUser
    ) {
        var data = userService.userData(loggedInUser);
        return ResponseEntity.ok(data);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequestDTO dto,
            @AuthenticationPrincipal User loggedInUser){
        userService.changePassword(loggedInUser, dto);
        return ResponseEntity.noContent().build();
    }
}
