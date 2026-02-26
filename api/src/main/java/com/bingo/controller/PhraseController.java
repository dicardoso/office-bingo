package com.bingo.controller;

import com.bingo.dto.PhraseRequest;
import com.bingo.model.BingoPhrase;
import com.bingo.service.PhraseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/phrases")
@RequiredArgsConstructor
public class PhraseController {

    private final PhraseService adminPhraseService;

    @GetMapping
    public ResponseEntity<List<BingoPhrase>> getAllPhrases() {
        return ResponseEntity.ok(adminPhraseService.getAllPhrases());
    }

    @PostMapping
    public ResponseEntity<BingoPhrase> createPhrase(@RequestBody PhraseRequest req) {
        return ResponseEntity.ok(adminPhraseService.createPhrase(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BingoPhrase> updatePhrase(@PathVariable String id, @RequestBody PhraseRequest req) {
        return ResponseEntity.ok(adminPhraseService.updatePhrase(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhrase(@PathVariable String id) {
        adminPhraseService.deletePhrase(id);
        return ResponseEntity.ok().build();
    }
}