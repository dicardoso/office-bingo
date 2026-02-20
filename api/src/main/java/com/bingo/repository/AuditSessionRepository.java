package com.bingo.repository;
import com.bingo.model.AuditSession;
import com.bingo.model.AuditStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuditSessionRepository extends MongoRepository<AuditSession, String> {
    Optional<AuditSession> findFirstByStatusOrderByStartTimeDesc(AuditStatus status);
}