package com.bingo.dto;

public record VoteRequest(
        String auditId,
        boolean vote // true = Confirma (Aconteceu), false = Nega (É Fake)
) {}