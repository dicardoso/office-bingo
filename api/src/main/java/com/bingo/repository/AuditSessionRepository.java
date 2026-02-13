package com.bingo.repository;
import com.bingo.model.AuditSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditSessionRepository extends MongoRepository<AuditSession, String> {
}