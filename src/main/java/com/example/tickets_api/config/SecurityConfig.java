package com.example.tickets_api.config;

import com.example.tickets_api.exceptions.ErrorResponse;
import tools.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

import java.time.Instant;

/**
 * Central Spring Security configuration: password hashing, the authentication
 * manager, and the HTTP authorization rules for the API.
 */
@Configuration
public class SecurityConfig {

    /**
     * Password hashing strategy used both to store seeded users and to verify
     * login attempts. BCrypt salts each hash automatically.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes Spring's {@link AuthenticationManager} so {@code AuthController}
     * can trigger username/password authentication on login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the request authorization rules and wires in JWT authentication.
     * <ul>
     *   <li>CSRF is disabled because the API is stateless (token-based, no cookies/sessions).</li>
     *   <li>{@code /login} and the Swagger docs are public; everything else requires authentication.</li>
     *   <li>{@code DELETE /tickets/**} additionally requires the ADMIN role.</li>
     *   <li>Sessions are STATELESS: each request must carry its own JWT.</li>
     *   <li>{@link JwtFilter} runs before the username/password filter to authenticate from the token.</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter, ObjectMapper objectMapper) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/tickets/**").hasRole("ADMIN")  // Only admins delete tickets
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ErrorResponse error = new ErrorResponse(
                                    HttpStatus.UNAUTHORIZED.value(),
                                    "Authentication required",
                                    Instant.now()
                            );
                            objectMapper.writeValue(response.getWriter(),error);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException)-> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ErrorResponse error = new ErrorResponse(
                                    HttpStatus.FORBIDDEN.value(),
                                    "Access denied",
                                    Instant.now()
                            );
                            objectMapper.writeValue(response.getWriter(),error);
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
