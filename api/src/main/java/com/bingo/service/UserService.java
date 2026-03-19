package com.bingo.service;

import com.bingo.dto.RoleUpdateRequest;
import com.bingo.dto.SeasonRankingResponse;
import com.bingo.dto.SuspendRequest;
import com.bingo.dto.XpUpdateRequest;
import com.bingo.model.User;
import com.bingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AdminGameService adminGameService;
    private final AuthService authService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateXp(String userId, XpUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        user.setCareerXp(req.careerXp());
        user.setSeasonXp(req.seasonXp());

        return userRepository.save(user);
    }

    public User updateRole(String userId, RoleUpdateRequest req) {
        User currentUser = authService.getCurrentUser();

        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Não pode alterar o seu próprio nível de acesso!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        user.setRole(req.role());
        return userRepository.save(user);
    }

    public User toggleSuspension(String userId, SuspendRequest req) {
        User currentUser = authService.getCurrentUser();

        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Não se pode suspender a si próprio!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        user.setSuspended(req.suspended());
        return userRepository.save(user);
    }

    public User applyTrollTheme(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        user.setPreferredTheme("troll");
        adminGameService.forceReload();
        return userRepository.save(user);
    }
    public User removeTrollTheme(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

        user.setPreferredTheme("default");
        adminGameService.forceReload();
        return userRepository.save(user);
    }

    public List<SeasonRankingResponse> getSeasonRanking() {
        return userRepository.findTop10ByOrderBySeasonXpDesc().stream()
                .map(u -> new SeasonRankingResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getPosition(),
                        u.getSeasonXp(),
                        u.getStats().getTotalBingos()
                ))
                .toList();
    }
}