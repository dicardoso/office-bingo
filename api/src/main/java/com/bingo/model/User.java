package com.bingo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String resetCode;
    private LocalDateTime resetCodeExpiration;
    private String role = "USER";
    private String preferredTheme = "dracula";
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastLoginDate = LocalDateTime.now();

    // --- CARREIRA ---
    @Builder.Default
    private Long careerXp = 0L;    // XP Vitalício

    @Builder.Default
    private String position = "Estagiário"; // Cargo atual

    // --- TEMPORADA ---
    @Builder.Default
    private Long seasonXp = 0L;    // XP do Mês (Resetável)

    // --- ESTATÍSTICAS ---
    @Builder.Default
    private UserStats stats = new UserStats();

    @Builder.Default
    private boolean suspended = false;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private int totalGamesPlayed = 0;
        private int totalBingos = 0;
        private int totalSlotsMarked = 0;
    }
}