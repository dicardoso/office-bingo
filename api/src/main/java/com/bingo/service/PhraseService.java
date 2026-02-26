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
        return phraseRepository.findAll();
    }

    public BingoPhrase createPhrase(PhraseRequest req) {
        // Futura regra de negócio pode entrar aqui (validar duplicidade)
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