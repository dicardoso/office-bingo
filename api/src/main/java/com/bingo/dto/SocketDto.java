package com.bingo.dto;

public class SocketDto {
    public record WinnerNotification(String username, String message, String timestamp) {}
    public record ProgressUpdate(String userId, String username, int markedCount) {}
}