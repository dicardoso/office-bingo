package com.bingo.controller;

import com.bingo.model.BingoCard;
import com.bingo.model.User;
import com.bingo.repository.UserRepository;
import com.bingo.service.BingoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final BingoService bingoService;
    private final UserRepository userRepository;

    private User getUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/my-card")
    public BingoCard getMyCard(Authentication auth) {
        return bingoService.getOrCreateDailyCard(getUser(auth));
    }

    @PostMapping("/mark/{position}")
    public BingoCard markSlot(Authentication auth, @PathVariable int position) {
        return bingoService.markSlot(getUser(auth), position);
    }

    @GetMapping("/leaderboard")
    public List<BingoCard> getLeaderboard() {
        return bingoService.getDailyLeaderboard();
    }

    @GetMapping("/card/{username}")
    public BingoCard getUserCard(@PathVariable String username) {
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bingoService.getOrCreateDailyCard(targetUser);
    }
}