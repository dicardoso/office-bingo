package com.bingo.service;

import com.bingo.model.User;
import com.bingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserRepository userRepository;

    private static final long XP_MARK_SLOT = 10;
    private static final long XP_BINGO_WIN = 150;

    public void processAction(User user, String actionType) {
        User.UserStats stats = user.getStats();
        if (stats == null) stats = new User.UserStats();

        switch (actionType) {
            case "MARK_SLOT" -> {
                applyXpChange(user, XP_MARK_SLOT);
                stats.setTotalSlotsMarked(stats.getTotalSlotsMarked() + 1);
            }
            case "UNMARK_SLOT" -> {
                applyXpChange(user, -XP_MARK_SLOT);
                stats.setTotalSlotsMarked(Math.max(0, stats.getTotalSlotsMarked() - 1));
            }
            case "BINGO_WIN" -> {
                applyXpChange(user, XP_BINGO_WIN);
                stats.setTotalBingos(stats.getTotalBingos() + 1);
            }
            case "BINGO_REVOKE" -> {
                applyXpChange(user, -XP_BINGO_WIN);
                stats.setTotalBingos(Math.max(0, stats.getTotalBingos() - 1));
            }
            case "CREATE_CARD" -> {
                stats.setTotalGamesPlayed(stats.getTotalGamesPlayed() + 1);
            }
        }

        user.setStats(stats);
        updatePosition(user);

        userRepository.save(user);
    }

    public void addXp(String userId, long amount) {
        updateUserXpById(userId, amount);
    }

    public void removeXp(String userId, long amount) {
        updateUserXpById(userId, -amount);
    }

    private void updateUserXpById(String userId, long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for XP update"));

        applyXpChange(user, amount);
        updatePosition(user);

        userRepository.save(user);
    }

    private void applyXpChange(User user, long amount) {
        long currentCareer = user.getCareerXp() != null ? user.getCareerXp() : 0;
        long currentSeason = user.getSeasonXp() != null ? user.getSeasonXp() : 0;

        long newCareer = Math.max(0, currentCareer + amount);
        long newSeason = Math.max(0, currentSeason + amount);
        user.setCareerXp(newCareer);
        user.setSeasonXp(newSeason);
    }

    private void updatePosition(User user) {
        long xp = user.getCareerXp();
        String newPosition;

        if (xp < 500) {
            newPosition = "Estagiário";
        } else if (xp < 2000) {
            newPosition = "Júnior";
        } else if (xp < 5000) {
            newPosition = "Pleno";
        } else if (xp < 10000) {
            newPosition = "Sênior";
        } else {
            newPosition = "Tech Lead";
        }

        user.setPosition(newPosition);
    }
}