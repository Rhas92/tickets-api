package com.example.tickets_api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Server's secret key. Minimum 32 characters for HS256.
    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour
    private final SecretKey key;

    public JwtService(@Value("${JWT_SECRET:my-super-secret-key-at-least-32-chars-long!!}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    // Generate the token (on login)
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    // Validate the token and extract the username (on each request)
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
