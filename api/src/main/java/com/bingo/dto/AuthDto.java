package com.bingo.dto;

import com.bingo.dto.UserDto;

public class AuthDto {
    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String email, String password, String code) {}
    public record AuthResponse(String token, UserDto.UserResponse user) {}
}