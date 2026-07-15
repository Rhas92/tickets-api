package com.example.tickets_api.controller;

import com.example.tickets_api.dto.TicketRequest;
import com.example.tickets_api.dto.TicketResponse;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

/**
 * REST endpoints for support tickets (the HTTP layer). Delegates all logic to
 * {@link TicketService} and converts between the {@link Ticket} entity and the
 * {@link TicketRequest}/{@link TicketResponse} DTOs so the API never exposes the
 * persistence model directly.
 */
@RestController
public class TicketController {
    private final TicketService ticketService;

    public TicketController (TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Lists tickets in a paginated form. Paging/sorting come from the request
     * (e.g. {@code ?page=0&size=20&sort=priority}).
     */
    @GetMapping("/tickets")
    public Page<TicketResponse> getTickets(Pageable pageable) {
        return ticketService.getTickets(pageable).map(this::toResponse);
    }

    /** Returns a single ticket, or 404 if no ticket has that id. */
    @GetMapping("/tickets/{id}")
    public TicketResponse getTicketById(@PathVariable String id) {
        return toResponse(ticketService.getTicketById(id));
    }

    /**
     * Returns a page of tickets in the given status. An unknown status value is
     * rejected as 400 by the global handler (type-mismatch on the enum).
     */
    @GetMapping("/tickets/status/{status}")
    public Page<TicketResponse> getTicketsByStatus(@PathVariable Status status, Pageable pageable) {
        return ticketService.getTicketsByStatus(status, pageable).map(this::toResponse);
    }

    /**
     * Returns a page of tickets with the given priority. An unknown priority value is
     * rejected as 400 by the global handler.
     */
    @GetMapping("/tickets/priority/{priority}")
    public Page<TicketResponse> getTicketsByPriority(@PathVariable Priority priority, Pageable pageable) {
        return ticketService.getTicketsByPriority(priority, pageable).map(this::toResponse);
    }

    /**
     * Creates a ticket from a validated payload and returns 201 with a
     * {@code Location} header pointing at the new resource.
     */
    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        Ticket created = ticketService.createTicket(toEntity(request));
        TicketResponse body = toResponse(created);
        URI location = URI.create("/tickets/" + created.getId());
        return ResponseEntity.created(location).body(body);
    }

    /**
     * Replaces the ticket at the given id with the validated payload, or 404 if
     * it does not exist.
     */
    @PutMapping("/tickets/{id}")
    public TicketResponse updateTicket(@Valid @RequestBody TicketRequest request, @PathVariable String id) {
        Ticket ticket = toEntity(request);
        return toResponse(ticketService.updateTicket(ticket, id));
    }
    /** Deletes the ticket and returns 204. Restricted to ADMIN by {@code SecurityConfig}. */
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    /** Maps an inbound request DTO to a new entity (id is assigned on save). */
    private Ticket toEntity(TicketRequest request) {
        return new Ticket(
                request.title(),
                request.description(),
                request.status(),
                request.priority()
        );
    }

    /** Maps an entity to the outbound response DTO returned to clients. */
    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority()
        );
    }
}
