package com.bingo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "bingo_cards")
public class BingoCard {
    @Id
    private String id;
    private String userId;
    private String username;
    private LocalDate gameDate;
    private boolean completed;
    private int markedCount;
    private List<Slot> slots;
    private LocalDateTime lastWinNotification;

    @Data
    public static class Slot {
        private int position;
        private String phrase;
        private boolean marked;
        private boolean verified = false;
    }
}