package com.bingo.service;

import com.bingo.dto.AuthDto.*;
import com.bingo.dto.UserDto;
import com.bingo.model.User;
import com.bingo.repository.UserRepository;
import com.bingo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Usuário já existe");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Este e-mail já está cadastrado em outra conta.");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreationDate(LocalDateTime.now());
        user.setLastLoginDate(LocalDateTime.now());
        user.setPosition("Novato");

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token, new UserDto.UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPosition(),
                savedUser.getPreferredTheme(),
                savedUser.getCareerXp(),
                savedUser.getSeasonXp(),
                savedUser.getStats() != null ? user.getStats() : new User.UserStats(),
                savedUser.getRole()
        ));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, new UserDto.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPosition(),
                user.getPreferredTheme(),
                user.getCareerXp(),
                user.getSeasonXp(),
                user.getStats(),
                user.getRole()
        ));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado no contexto.");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));
    }

    public void updateTheme(User user, String newTheme) {
        user.setPreferredTheme(newTheme);

        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return;

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        String hashedCode = passwordEncoder.encode(code);

        user.setResetCode(hashedCode);
        user.setResetCodeExpiration(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), code);
    }

    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Dados inválidos."));

        if (user.getResetCode() == null || !passwordEncoder.matches(code, user.getResetCode())) {
            throw new IllegalArgumentException("Código de verificação inválido.");
        }

        if (user.getResetCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("O código expirou. Solicite um novo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetCode(null);
        user.setResetCodeExpiration(null);
        userRepository.save(user);
    }
}