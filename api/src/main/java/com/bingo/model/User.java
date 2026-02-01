package com.bingo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String role = "USER";
    private String position;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastLoginDate = LocalDateTime.now();
}