package com.bingo.controller;

import com.bingo.dto.BroadcastRequest;
import com.bingo.service.AdminGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/game")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminGameController {

    private final AdminGameService adminGameService;

    @PostMapping("/reset-season")
    public ResponseEntity<Void> resetSeason() {
        adminGameService.resetSeason();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/force-cards")
    public ResponseEntity<Void> forceNewCards() {
        adminGameService.forceNewCards();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastMessage(@RequestBody BroadcastRequest req) {
        if (req == null || req.message() == null || req.message().isBlank()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        adminGameService.broadcastMessage(req.message());
        return ResponseEntity.ok().build();
    }
}