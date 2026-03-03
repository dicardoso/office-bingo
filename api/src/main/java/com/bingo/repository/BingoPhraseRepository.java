package com.bingo.repository;

import com.bingo.model.BingoPhrase;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface BingoPhraseRepository extends MongoRepository<BingoPhrase, String> {
    List<BingoPhrase> findByActiveTrue();

    List<BingoPhrase> findByCategoryAndActiveTrue(String category);

    Optional<BingoPhrase> findByText(String text);
    List<BingoPhrase> findAllByOrderByTextAsc();
    Optional<BingoPhrase> findByTextIgnoreCase(String text);
}