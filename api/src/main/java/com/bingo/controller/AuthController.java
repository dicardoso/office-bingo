package com.bingo.controller;

import com.bingo.dto.AuthDto.*;
import com.bingo.dto.UserDto;
import com.bingo.model.User;
import com.bingo.repository.UserRepository;
import com.bingo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Usuário já existe");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreationDate(LocalDateTime.now());
        user.setLastLoginDate(LocalDateTime.now());
        user.setPosition("");
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, new UserDto.UserResponse(user.getUsername(), user.getUsername(), user.getPosition()));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }
        user.setLastLoginDate(LocalDateTime.now());
        String token = jwtService.generateToken(user.getUsername());
        userRepository.save(user);
        return new AuthResponse(token, new UserDto.UserResponse(user.getId(), user.getUsername(), user.getPosition()));
    }
}