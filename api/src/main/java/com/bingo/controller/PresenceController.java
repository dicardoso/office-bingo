package com.bingo.controller;

import com.bingo.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @MessageMapping("/presence/connect")
    public void userConnected(@Payload String payload, SimpMessageHeaderAccessor headerAccessor) {
        String userId = payload.replace("\"", "");

        System.out.println("✅ Usuário conectado no Socket: " + userId);

        String sessionId = headerAccessor.getSessionId();
        presenceService.addUserConnection(sessionId, userId);
    }
}