package com.example.tickets_api.dto;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Status status,
        @NotNull Priority priority
) {}