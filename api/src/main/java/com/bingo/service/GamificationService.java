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
                addXp(user, XP_MARK_SLOT);
                stats.setTotalSlotsMarked(stats.getTotalSlotsMarked() + 1);
            }
            case "UNMARK_SLOT" -> {
                removeXp(user);
                stats.setTotalSlotsMarked(Math.max(0, stats.getTotalSlotsMarked() - 1));
            }
            case "BINGO_WIN" -> {
                addXp(user, XP_BINGO_WIN);
                stats.setTotalBingos(stats.getTotalBingos() + 1);
            }
            case "BINGO_REVOKE" -> {
                removeXp(user, XP_BINGO_WIN);
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

    private void addXp(User user, long amount) {
        user.setCareerXp(user.getCareerXp() + amount);
        user.setSeasonXp(user.getSeasonXp() + amount);
    }

    private void removeXp(User user, long amount) {
        long newCareer = Math.max(0, user.getCareerXp() - amount);
        long newSeason = Math.max(0, user.getSeasonXp() - amount);

        user.setCareerXp(newCareer);
        user.setSeasonXp(newSeason);
    }

    private void removeXp(User user) {
        long newCareer = Math.max(0, user.getCareerXp() - GamificationService.XP_MARK_SLOT);
        long newSeason = Math.max(0, user.getSeasonXp() - GamificationService.XP_MARK_SLOT);

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