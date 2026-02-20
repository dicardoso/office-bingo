package com.bingo.dto;

public record AuditRequest(
        String accusedId,
        int slotPosition
) {}