package com.example.tickets_api.dto;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Inbound payload for creating or updating a ticket. The bean-validation
 * constraints are enforced where the controller method is annotated with
 * {@code @Valid}; violations are reported as 400 by the global handler. This is
 * also where the "must not be blank" rule actually lives — not on the entity.
 *
 * @param title       short summary; 3–100 characters, must not be blank
 * @param description detailed explanation; up to 2000 characters, must not be blank
 * @param status      lifecycle state; must not be null
 * @param priority    urgency level; must not be null
 */
public record TicketRequest(
        @NotBlank @Size(min = 3, max = 100) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotNull Status status,
        @NotNull Priority priority
) {}