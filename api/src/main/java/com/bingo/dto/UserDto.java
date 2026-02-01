package com.bingo.dto;

import com.bingo.model.User;

public class UserDto {
    public record UserResponse(String id, String username, String position) {}
}