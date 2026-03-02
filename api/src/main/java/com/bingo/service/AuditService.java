package com.bingo.service;

import com.bingo.dto.AuditRequest;
import com.bingo.dto.VoteRequest;
import com.bingo.model.AuditSession;
import com.bingo.model.AuditStatus;
import com.bingo.model.BingoCard;
import com.bingo.model.User;
import com.bingo.repository.AuditSessionRepository;
import com.bingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditService {

    // Constantes de XP
    private static final long XP_PENALTY_FRAUD = 50;  // Acusado mentiu
    private static final long XP_BONUS_JUSTICE = 50;  // Auditor estava certo
    private static final long XP_PENALTY_SLANDER = 100; // Auditor mentiu (Calúnia)
    private static final long XP_BONUS_DAMAGES = 20;   // Acusado era inocente

    // Dependências
    private final SimpMessagingTemplate socket;
    private final GamificationService gamification;
    private final BingoService cardService;
    private final TaskScheduler taskScheduler;
    private final UserRepository  userRepository;
    private final AuditSessionRepository auditRepository;

    public AuditSession startAudit(AuditRequest req, User auditor) {
        if (auditor.isSuspended()) {
            throw new IllegalStateException("Contas suspensas perderam o direito de abrir processos no Tribunal.");
        }

        if (req.accusedId().equals(auditor.getId())) {
            throw new IllegalArgumentException("Você não pode se auto-auditar (conflito de interesse).");
        }

        User accused = userRepository.findById(req.accusedId())
                .orElseThrow(() -> new IllegalArgumentException("Acusado não encontrado"));

        BingoCard card = cardService.getOrCreateDailyCard(accused);

        BingoCard.Slot targetSlot = card.getSlots().stream()
                .filter(s -> s.getPosition() == req.slotPosition())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Posição inválida na cartela."));

        if (!targetSlot.isMarked()) {
            throw new IllegalArgumentException("Este item ainda não foi marcado e não pode ser auditado.");
        }

        if (targetSlot.isVerified()) {
            throw new IllegalArgumentException("Este item já foi verificado e é verdade absoluta!");
        }

        AuditSession session = AuditSession.builder()
                .auditorId(auditor.getId())
                .auditorName(auditor.getUsername())
                .accusedId(accused.getId())
                .accusedName(accused.getUsername())
                .slotPosition(req.slotPosition())
                .slotPhrase(targetSlot.getPhrase())
                .startTime(LocalDateTime.now())
                .status(AuditStatus.OPEN)
                // .votes()
                .build();

        session = auditRepository.save(session);

        socket.convertAndSend("/topic/audit/start", session);

        String auditId = session.getId();
        taskScheduler.schedule(
                () -> closeAudit(auditId),
                Instant.now().plusSeconds(60)
        );

        return session;
    }

    public void processVote(VoteRequest req, User voter) {
        if (voter.isSuspended()) {
            throw new IllegalStateException("Contas suspensas não têm direito a voto.");
        }
        AuditSession session = auditRepository.findById(req.auditId())
                .orElseThrow(() -> new IllegalArgumentException("Sessão de auditoria não encontrada."));

        if (session.getStatus() != AuditStatus.OPEN) {
            throw new IllegalStateException("Esta votação já foi encerrada.");
        }

        if (voter.getId().equals(session.getAuditorId())) {
            throw new IllegalArgumentException("O Auditor não pode votar no próprio processo.");
        }
        if (voter.getId().equals(session.getAccusedId())) {
            throw new IllegalArgumentException("O Acusado não pode votar em sua defesa.");
        }

        session.getVotes().put(voter.getId(), req.vote());

        session = auditRepository.save(session);

        socket.convertAndSend("/topic/audit/update", session);

        // (Opcional) Lógica de Encerramento Antecipado "Mercy Rule"
        // Se a diferença de votos for absurda (ex: 5 a 0), poderiamos fechar.
        // Por enquanto, vamos deixar o Timer de 60s ser o juiz final para simplificar.
    }

    public void closeAudit(String auditId) {
        Optional<AuditSession> optionalSession = auditRepository.findById(auditId);
        if (optionalSession.isEmpty()) {
            return;
        }
        AuditSession session = optionalSession.get();

        if (session.getStatus() != AuditStatus.OPEN) return;

        long votesTrue = session.getVotes().values().stream().filter(v -> v).count();
        long votesFalse = session.getVotes().values().stream().filter(v -> !v).count();
        boolean isGuilty = votesFalse > votesTrue;

        if (isGuilty) {
            session.setStatus(AuditStatus.GUILTY);
            applyPunishment(session);
        } else {
            session.setStatus(AuditStatus.INNOCENT);
            applyCompensation(session);
        }

        auditRepository.save(session);
        socket.convertAndSend("/topic/audit/end", session);
    }

    private void applyPunishment(AuditSession session) {
        cardService.forceUnmark(session.getAccusedId(), session.getSlotPosition());

        gamification.removeXp(session.getAccusedId(), XP_PENALTY_FRAUD);
        gamification.addXp(session.getAuditorId(), XP_BONUS_JUSTICE);
    }

    private void applyCompensation(AuditSession session) {
        gamification.addXp(session.getAccusedId(), XP_BONUS_DAMAGES);
        gamification.removeXp(session.getAuditorId(), XP_PENALTY_SLANDER);
        cardService.markAsVerified(session.getAccusedId(), session.getSlotPosition());
    }

    public Optional<AuditSession> getCurrentOpenAudit() {
        return auditRepository.findFirstByStatusOrderByStartTimeDesc(AuditStatus.OPEN);
    }
}