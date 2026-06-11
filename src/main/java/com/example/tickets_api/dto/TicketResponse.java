package com.example.tickets_api.dto;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;

public record TicketResponse(
        String id,
        String title,
        String description,
        Status status,
        Priority priority
) {}