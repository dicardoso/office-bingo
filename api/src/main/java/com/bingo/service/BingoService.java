package com.bingo.service;

import com.bingo.dto.SocketDto.*;
import com.bingo.model.*;
import com.bingo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BingoService {

    private final BingoCardRepository cardRepository;
    private final AuditLogRepository auditLogRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Lista de frases (idealmente viria de um DB ou JSON config)
    private static final List<String> PHRASES = Arrays.asList(
            "Na minha máquina funciona", "Reinicia o servidor", "É cache",
            "Caiu a internet", "Conflito no merge", "Falta café",
            "Vou abrir chamado", "Feature, não bug", "Testou em prod?",
            "Deploy sexta-feira", "Culpa do estagiário", "Backlog tá cheio",
            "Reunião que podia ser email", "O Docker morreu", "Git blame"
    );

    public BingoCard getOrCreateDailyCard(User user) {
        LocalDate today = LocalDate.now();
        return cardRepository.findByUserIdAndGameDate(user.getId(), today)
                .orElseGet(() -> createNewCard(user, today));
    }

    private BingoCard createNewCard(User user, LocalDate date) {
        List<String> shuffled = new ArrayList<>(PHRASES);
        Collections.shuffle(shuffled);

        List<BingoCard.Slot> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            BingoCard.Slot slot = new BingoCard.Slot();
            slot.setPosition(i);
            slot.setPhrase(shuffled.get(i));
            slot.setMarked(false);
            slots.add(slot);
        }

        BingoCard card = new BingoCard();
        card.setUserId(user.getId());
        card.setUsername(user.getUsername());
        card.setGameDate(date);
        card.setSlots(slots);
        card.setMarkedCount(0);

        logAction(user.getId(), "CREATE_CARD", "Data: " + date);
        return cardRepository.save(card);
    }

    public BingoCard markSlot(User user, int position) {
        BingoCard card = getOrCreateDailyCard(user);

        boolean changed = false;
        for (BingoCard.Slot slot : card.getSlots()) {
            if (slot.getPosition() == position) {
                boolean novoEstado = !slot.isMarked();
                slot.setMarked(novoEstado);

                if (novoEstado) {
                    card.setMarkedCount(card.getMarkedCount() + 1);
                    logAction(user.getId(), "MARK_SLOT", slot.getPhrase());
                } else {
                    card.setMarkedCount(Math.max(0, card.getMarkedCount() - 1));
                    logAction(user.getId(), "UNMARK_SLOT", slot.getPhrase());
                }

                changed = true;
                break;
            }
        }

        if (changed) {
            boolean isBingoNow = checkWinCondition(card);
            boolean wasCompletedBefore = card.isCompleted();

            if (isBingoNow && !wasCompletedBefore) {
                logAction(user.getId(), "BINGO_WIN", "Ganhou o jogo!");

                WinnerNotification winner = new WinnerNotification(
                        user.getUsername(),
                        "BINGO! " + user.getUsername() + " fechou a cartela!",
                        LocalDateTime.now().toString()
                );
                messagingTemplate.convertAndSend("/topic/winners", winner);
            }

            card.setCompleted(isBingoNow);

            BingoCard saved = cardRepository.save(card);

            ProgressUpdate update = new ProgressUpdate(user.getId(), user.getUsername(), saved.getMarkedCount());
            messagingTemplate.convertAndSend("/topic/progress", update);

            return saved;
        }
        return card;
    }

    public List<BingoCard> getDailyLeaderboard() {
        return cardRepository.findAllByGameDateOrderByMarkedCountDesc(LocalDate.now());
    }

    private void logAction(String userId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .action(action)
                .details(details)
                .build());
    }

    private boolean checkWinCondition(BingoCard card) {
        boolean[] m = new boolean[9];
        card.getSlots().forEach(s -> m[s.getPosition()] = s.isMarked());
        // Linhas
        if (m[0] && m[1] && m[2]) return true;
        if (m[3] && m[4] && m[5]) return true;
        if (m[6] && m[7] && m[8]) return true;
        // Colunas
        if (m[0] && m[3] && m[6]) return true;
        if (m[1] && m[4] && m[7]) return true;
        if (m[2] && m[5] && m[8]) return true;
        // Diagonais
        if (m[0] && m[4] && m[8]) return true;
        if (m[2] && m[4] && m[6]) return true;
        return false;
    }
}