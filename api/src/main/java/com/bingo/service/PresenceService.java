package com.bingo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final SimpMessagingTemplate socket;

    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    private final Map<String, Integer> activeUsersCount = new ConcurrentHashMap<>();

    public void addUserConnection(String sessionId, String userId) {
        sessionUserMap.put(sessionId, userId);
        activeUsersCount.put(userId, activeUsersCount.getOrDefault(userId, 0) + 1);

        broadcastOnlineUsers();
    }

    public Set<String> getOnlineUsers() {
        return activeUsersCount.keySet();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String userId = sessionUserMap.remove(sessionId);

        if (userId != null) {
            int count = activeUsersCount.getOrDefault(userId, 1) - 1;
            if (count <= 0) {
                activeUsersCount.remove(userId);
            } else {
                activeUsersCount.put(userId, count);
            }
            broadcastOnlineUsers();
        }
    }

    private void broadcastOnlineUsers() {
        socket.convertAndSend("/topic/presence", getOnlineUsers());
    }
}