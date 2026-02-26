package com.bingo.security;

import com.bingo.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {
    @Value("${bingo.jwt.secret}")
    private String secret;
    private final ZoneId zoneId = ZoneId.systemDefault();

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("role", user.getRole())
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(getEndOfDay())
                .signWith(getSignKey())
                .compact();
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    private Date getEndOfDay() {
        return Date.from(LocalDate.now(zoneId)
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant());
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}