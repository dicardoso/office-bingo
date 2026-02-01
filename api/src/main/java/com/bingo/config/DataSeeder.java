package com.bingo.config;

import com.bingo.model.BingoPhrase;
import com.bingo.repository.BingoPhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BingoPhraseRepository phraseRepository;

    @Override
    public void run(String... args) {
        if (phraseRepository.count() == 0) {
            System.out.println("--- DB Vazio: Populando frases iniciais ---");

            List<String> defaultPhrases = Arrays.asList(
                    "Na minha máquina funciona", "Reinicia o servidor", "É cache",
                    "Caiu a internet", "Conflito no merge", "Falta café",
                    "Vou abrir chamado", "Feature, não bug", "Testou em prod?",
                    "Deploy sexta-feira", "Culpa do estagiário", "Backlog tá cheio",
                    "Reunião que podia ser email", "O Docker morreu", "Git blame",
                    "Esqueci o ponto e vírgula", "Erro de DNS", "O cliente mudou o escopo"
            );

            List<BingoPhrase> entities = defaultPhrases.stream()
                    .map(text -> BingoPhrase.builder()
                            .text(text)
                            .category("GERAL")
                            .active(true)
                            .build())
                    .toList();

            phraseRepository.saveAll(entities);
        }
    }
}