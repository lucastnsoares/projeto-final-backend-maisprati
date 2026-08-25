package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.UserCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.UserUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.service.AdminService;
import br.com.maisprati.projeto.service.RegisterUserService;
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
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor

public class AdminController {
    private final AdminService adminService;
    private final RegisterUserService registerUserService;


    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> listUsers(
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {
        var page = adminService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping ("/users")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid UserCreateRequestDTO dto
            ) {
        var newUser = registerUserService.registerUserAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(
            @PathVariable Long id
    ) {
        var user = adminService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDTO dto
    ) {
        var userUpdated = adminService.updateUser(dto, id);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdated);
    }




}
