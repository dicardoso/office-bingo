package com.bingo.repository;

import com.bingo.model.OtpRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OtpRegistrationRepository extends MongoRepository<OtpRegistration, String> {
    Optional<OtpRegistration> findByEmail(String email);
    void deleteByEmail(String email);
}