package com.bingo.service;

import com.bingo.dto.PhraseRequest;
import com.bingo.model.BingoPhrase;
import com.bingo.repository.BingoPhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhraseService {

    private final BingoPhraseRepository phraseRepository;

    public List<BingoPhrase> getAllPhrases() {
        return phraseRepository.findAllByOrderByTextAsc();
    }

    public BingoPhrase createPhrase(PhraseRequest req) {
        phraseRepository.findByTextIgnoreCase(req.text())
                .ifPresent(p -> {
                    throw new IllegalStateException("Esta frase já está cadastrada no sistema.");
                });

        BingoPhrase phrase = new BingoPhrase();
        phrase.setText(req.text());
        phrase.setActive(req.active());

        return phraseRepository.save(phrase);
    }

    public BingoPhrase updatePhrase(String id, PhraseRequest req) {
        BingoPhrase phrase = phraseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Frase não encontrada."));

        phrase.setText(req.text());
        phrase.setActive(req.active());

        return phraseRepository.save(phrase);
    }

    public void deletePhrase(String id) {
        phraseRepository.deleteById(id);
    }
}