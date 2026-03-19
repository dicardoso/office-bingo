package com.bingo.service;

import com.bingo.model.User;
import com.bingo.repository.BingoCardRepository;
import com.bingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminGameService {

    private final UserRepository userRepository;
    private final BingoCardRepository cardRepository;
    private final SimpMessagingTemplate socket;

    public void resetSeason() {
        List<User> users = userRepository.findAll();

        users.forEach(u -> u.setSeasonXp(0L));
        userRepository.saveAll(users);

        broadcastMessage("🏆 NOVA TEMPORADA! O XP Mensal foi zerado. Que comecem os jogos!");
    }

    public void forceNewCards() {
        cardRepository.deleteByGameDate(LocalDate.now());

        socket.convertAndSend("/topic/broadcast", Map.of("type", "RELOAD_CARDS", "message", "O Game Master embaralhou as cartelas! Sorteando novas frases..."));
    }

    public void forceReload() {
        socket.convertAndSend("/topic/broadcast", Map.of("type", "RELOAD_PAGE", "message", "O Game Master recarregou sua página"));
    }

    public void broadcastMessage(String message) {
        socket.convertAndSend("/topic/broadcast", Map.of("type", "ALERT", "message", message));
    }
}