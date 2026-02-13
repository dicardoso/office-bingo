package com.bingo.controller;

import com.bingo.dto.AuditRequest;
import com.bingo.dto.VoteRequest;
import com.bingo.model.AuditSession;
import com.bingo.model.User;
import com.bingo.service.AuditService;
import com.bingo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final AuthService authService;

    @PostMapping("/initiate")
    public ResponseEntity<AuditSession> initiateAudit(@RequestBody AuditRequest request) {
        System.out.println("Iniciando Auditor");
        User auditor = authService.getCurrentUser();
        return ResponseEntity.ok(auditService.startAudit(request, auditor));
    }

    @PostMapping("/vote")
    public ResponseEntity<Void> castVote(@RequestBody VoteRequest request) {
        User voter = authService.getCurrentUser();
        auditService.processVote(request, voter);
        return ResponseEntity.ok().build();
    }
}