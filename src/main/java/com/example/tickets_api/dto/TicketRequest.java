package com.example.tickets_api.dto;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Inbound payload for creating or updating a ticket. The bean-validation
 * constraints are enforced where the controller method is annotated with
 * {@code @Valid}; violations are reported as 400 by the global handler. This is
 * also where the "must not be blank" rule actually lives — not on the entity.
 *
 * @param title       short summary; must not be blank
 * @param description detailed explanation; must not be blank
 * @param status      lifecycle state; must not be null
 * @param priority    urgency level; must not be null
 */
public record TicketRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Status status,
        @NotNull Priority priority
) {}