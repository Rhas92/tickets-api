package com.example.tickets_api.dto;

/**
 * Credentials submitted to {@code POST /login}.
 *
 * @param username the account's username
 * @param password the account's plain-text password (verified against the stored hash)
 */
public record LoginRequest(String username, String password) {}