package com.example.tickets_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration. Declares a single "bearerAuth" security scheme
 * (HTTP Bearer, JWT format) and applies it to the whole API, so the generated
 * docs show the <em>Authorize</em> button and send the token on every try-it-out
 * request. This only affects the documentation UI; the real enforcement lives in
 * {@link SecurityConfig}.
 */
@Configuration
@OpenAPIDefinition(security = { @SecurityRequirement(name = "bearerAuth") })
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
