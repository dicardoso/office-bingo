package com.bingo.repository;

import com.bingo.model.BingoCard;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BingoCardRepository extends MongoRepository<BingoCard, String> {
    Optional<BingoCard> findByUserIdAndGameDate(String userId, LocalDate gameDate);
    List<BingoCard> findAllByGameDateOrderByMarkedCountDesc(LocalDate gameDate);
}