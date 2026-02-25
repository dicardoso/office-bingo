package com.bingo.dto;

import com.bingo.model.User;

public class UserDto {
    public record UserResponse(String id, String username, String position, String preferredTheme, Long careerXp,Long seasonXp,User.UserStats stats) {
        public static UserResponse fromUser(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getPosition(),
                    user.getPreferredTheme(),
                    user.getCareerXp() != null ? user.getCareerXp() : 0L,
                    user.getSeasonXp() != null ? user.getSeasonXp() : 0L,
                    user.getStats() != null ? user.getStats() : new User.UserStats()
            );
        }
    }
}