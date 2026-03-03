package com.bingo.dto;

public record SeasonRankingResponse(
        String id,
        String username,
        String position,
        Long seasonXp,
        int totalBingos
) {}