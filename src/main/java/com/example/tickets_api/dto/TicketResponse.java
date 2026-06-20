package com.example.tickets_api.dto;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;

/**
 * Outbound representation of a ticket returned to clients. Decouples the API from
 * the persistence model: only these fields are exposed, never the entity itself.
 *
 * @param id          the ticket's unique identifier
 * @param title       short summary of the issue
 * @param description detailed explanation of the issue
 * @param status      current lifecycle state
 * @param priority    urgency level
 */
public record TicketResponse(
        String id,
        String title,
        String description,
        Status status,
        Priority priority
) {}