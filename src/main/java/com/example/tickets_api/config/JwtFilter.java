package com.example.tickets_api.config;

import com.example.tickets_api.service.CustomUserDetailsService;
import com.example.tickets_api.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates requests carrying a JWT. Runs once per request (extends
 * {@link OncePerRequestFilter}) before Spring's authorization rules are applied.
 * <p>
 * Its only job is to <em>populate</em> the {@link SecurityContextHolder} when a
 * valid token is present. It never rejects a request on its own: a missing or
 * invalid token simply leaves the request unauthenticated, and the URL-based
 * security rules decide whether that results in 401/403 or is allowed through.
 * This keeps authentication (who you are) and authorization (what you may do)
 * cleanly separated.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Extracts a Bearer token, validates it and, if valid, marks the request as
     * authenticated for the duration of the request. Always continues the filter
     * chain so downstream rules run regardless of the outcome.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No token -> let it pass; the security rules decide.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " (7 chars) to get the raw token.
        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Tell Spring: this request is authenticated as this user.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException e) {
            // Invalid/expired token -> stay unauthenticated; protected endpoints will reject.
        }

        filterChain.doFilter(request, response);
    }
}
