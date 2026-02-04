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
        System.out.println("--- Verificando e Atualizando Frases (Upsert) ---");

        // 1. Lista Técnica
        List<String> techPhrases = Arrays.asList(
                "Na minha máquina funciona",
                "Reinicia o servidor que volta",
                "É problema de cache/cookie",
                "Caiu a internet (ou o VPN)",
                "Conflito chato no merge",
                "Deploy em sexta-feira",
                "Culpa do estagiário",
                "O GIT morreu",
                "Comentou código em vez de apagar",
                "Push direto na master/main",
                "Erro de DNS",
                "Foi culpa do Firewall"
        );

        // 2. Lista Comportamental
        List<String> behaviorPhrases = Arrays.asList(
                "Alguém trouxe bolo/lanche",            // (Adilson comeu abacaxi)
                "PC desbloqueado (Cuidado!)",           // (Alguém deixou o PC destravado)
                "Risada escandalosa na sala/call",      // (João Pedro riu como hiena)
                "Suspiro profundo de estresse",         // (Letícia se estressou)
                "Dev teimoso ('Sempre fiz assim')",     // (Diogo falou só faço assim)
                "Alguém começou a cantar do nada",      // (Cantou Manuel Gomes)
                "Barulho estranho (gemido/latido?)",    // (Alic latiu/Alan gemeu)
                "Chegou gadget novo da China",          // (Manuel comprou algo novo)
                "Momento Coach / LinkedIn",             // (Alan falou algo inspirador)
                "Patada gratuita em alguém",            // (Diogo deu uma patada)
                "Explicação técnica infinita",          // (Manuel foi prolixo)
                "Roubaram minha cadeira/cabo",          // (João Pedro furtou algo)
                "Rejeitou a task no Review",            // (Walter rejeitou task)
                "Olhar de julgamento silencioso",       // (Letícia cara de nojo)
                "Subestimou a task ('É fácil')",   // (Hugo disse que é liso)
                "Chegou de óculos escuro (Ressaca?)",   // (Alic entrou de óculos)
                "Fofoca no cantinho do café",           // (Letícia se meteu na vida)
                "Levou crédito pelo bug fix alheio"     // (Diogo agradeceu algo que não fez)
        );

        processPhrases(techPhrases, "TECNICO");
        processPhrases(behaviorPhrases, "COMPORTAMENTO");

        System.out.println("--- Sincronização de frases concluída ---");
    }

    private void processPhrases(List<String> phrases, String category) {
        for (String text : phrases) {
            // Lógica de UPSERT: Busca pelo texto
            BingoPhrase phrase = phraseRepository.findByText(text)
                    .orElseGet(() -> BingoPhrase.builder()
                            .text(text)
                            .build());

            phrase.setCategory(category);
            phrase.setActive(true);

            phraseRepository.save(phrase);
        }
    }
}