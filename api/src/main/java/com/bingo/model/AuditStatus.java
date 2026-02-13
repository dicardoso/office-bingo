package com.bingo.model;

public enum AuditStatus {
    OPEN,           // Votação rolando
    GUILTY,         // O acusado mentiu (A frase NÃO aconteceu)
    INNOCENT,       // O acusado falou a verdade (A frase ACONTECEU)
    CANCELED        // Cancelado por falta de quórum ou erro
}