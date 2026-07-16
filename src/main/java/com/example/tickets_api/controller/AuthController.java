package com.example.tickets_api.controller;

import com.example.tickets_api.dto.AuthResponse;
import com.example.tickets_api.dto.LoginRequest;
import com.example.tickets_api.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoint. Exchanges valid credentials for a JWT that clients
 * then send on every protected request.
 */
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Verifies the credentials and, on success, returns a freshly signed JWT.
     * If authentication fails, {@code authenticate} throws and the global handler
     * maps it to 401 — so a token is only issued for valid credentials.
     *
     * @param request the username and password
     * @return a response wrapping the signed JWT
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        return new AuthResponse(jwtService.generateToken(request.username()));
    }
}
