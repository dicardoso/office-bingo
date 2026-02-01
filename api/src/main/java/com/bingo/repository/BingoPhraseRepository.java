package com.bingo.repository;

import com.bingo.model.BingoPhrase;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BingoPhraseRepository extends MongoRepository<BingoPhrase, String> {
    List<BingoPhrase> findByActiveTrue();

    List<BingoPhrase> findByCategoryAndActiveTrue(String category);
}