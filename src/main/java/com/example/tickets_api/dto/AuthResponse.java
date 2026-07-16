package com.example.tickets_api.dto;

/**
 * Response body of a successful login: the signed JWT the client must send on
 * subsequent protected requests.
 *
 * @param token the signed JSON Web Token
 */
public record AuthResponse(String token) {
}
