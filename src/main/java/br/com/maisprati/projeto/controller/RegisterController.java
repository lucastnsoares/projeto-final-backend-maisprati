package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.UserRegisterRequestDTO;
import br.com.maisprati.projeto.dto.response.UserSummaryResponseDTO;
import br.com.maisprati.projeto.service.RegisterUserService;
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
public class RegisterController {
    private final RegisterUserService registerUserService;

    @PostMapping
    public ResponseEntity<UserSummaryResponseDTO> registerUser(@RequestBody @Valid UserRegisterRequestDTO dto) {
        var userCreatedDTO = registerUserService.registerUserPublic(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreatedDTO);
    }

}
