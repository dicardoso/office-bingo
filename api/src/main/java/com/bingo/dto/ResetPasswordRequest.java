package com.bingo.dto;

public record ResetPasswordRequest(String email, String code, String newPassword) {}
