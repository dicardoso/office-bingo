package com.bingo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bingo_phrases")
public class BingoPhrase {
    @Id
    private String id;

    private String text;
    private String category;

    @Builder.Default
    private boolean active = true;
}