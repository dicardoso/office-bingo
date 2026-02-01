package com.bingo.repository;
import com.bingo.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {}