package com.example.tickets_api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Issues and validates JWTs used for stateless authentication. Tokens are signed
 * with HS256 using a secret loaded from configuration.
 * <p>
 * Security note: the {@code JWT_SECRET} default below is a placeholder for local
 * use only. Anyone who knows it can forge valid tokens, so production must supply
 * its own secret via the environment. (Removing this fallback so a missing secret
 * fails fast is tracked as a pending hardening task.)
 */
@Service
public class JwtService {

    // Server's secret key. Minimum 32 characters for HS256.
    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour
    private final SecretKey key;

    /**
     * Builds the signing key from the configured secret.
     *
     * @param secret the HMAC secret; must be at least 32 bytes for HS256
     */
    public JwtService(@Value("${JWT_SECRET}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Mints a signed token for the given user, valid for one hour.
     *
     * @param username the subject the token represents
     * @return the compact, signed JWT string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    /**
     * Verifies a token's signature and expiry and returns its subject.
     *
     * @param token the compact JWT string
     * @return the username stored in the token's subject
     * @throws io.jsonwebtoken.JwtException if the token is invalid, tampered or expired
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
