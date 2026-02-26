package com.bingo.controller;

import com.bingo.dto.AuthDto.*;
import com.bingo.dto.ThemeRequest;
import com.bingo.dto.UserDto;
import com.bingo.model.User;
import com.bingo.repository.UserRepository;
import com.bingo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto.UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(UserDto.UserResponse.fromUser(user));
    }

    @PatchMapping("/theme")
    public ResponseEntity<Void> updateTheme(@RequestBody ThemeRequest request) {
        User currentUser = authService.getCurrentUser();
        authService.updateTheme(currentUser, request.theme());

        return ResponseEntity.ok().build();
    }
}