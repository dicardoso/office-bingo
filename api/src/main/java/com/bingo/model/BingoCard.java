package com.bingo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
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

    @Data
    public static class Slot {
        private int position; // 0-8
        private String phrase;
        private boolean marked;
    }
}