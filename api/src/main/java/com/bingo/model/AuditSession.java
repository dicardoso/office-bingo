package com.bingo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_sessions")
public class AuditSession {
    @Id
    private String id;

    private String auditorId;
    private String auditorName;

    private String accusedId;
    private String accusedName;

    private int slotPosition;
    private String slotPhrase;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private AuditStatus status;

    @Builder.Default
    private Map<String, Boolean> votes = new HashMap<>();
}