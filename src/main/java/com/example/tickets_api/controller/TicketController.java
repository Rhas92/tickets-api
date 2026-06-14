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

@RestController
public class TicketController {
    private final TicketService ticketService;

    public TicketController (TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public Page<TicketResponse> getTickets(Pageable pageable) {
        return ticketService.getTickets(pageable).map(this::toResponse);
    }
    @GetMapping("/tickets/status/{status}")
    public List<TicketResponse> getTicketsByStatus(@PathVariable Status status) {
        return ticketService.getTicketsByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }
    @GetMapping("/tickets/{id}")
    public TicketResponse getTicketById(@PathVariable String id) {
        return toResponse(ticketService.getTicketById(id));
    }
    @GetMapping("/tickets/priority/{priority}")
    public List<TicketResponse> getTicketsByPriority(@PathVariable Priority priority) {
        return ticketService.getTicketsByPriority(priority).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        Ticket created = ticketService.createTicket(toEntity(request));
        TicketResponse body = toResponse(created);
        URI location = URI.create("/tickets/" + created.getId());
        return ResponseEntity.created(location).body(body);
    }
    @PutMapping("/tickets/{id}")
    public TicketResponse updateTicket(@Valid @RequestBody TicketRequest request, @PathVariable String id) {
        Ticket ticket = toEntity(request);
        return toResponse(ticketService.updateTicket(ticket, id));
    }
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    private Ticket toEntity(TicketRequest request) {
        return new Ticket(
                request.title(),
                request.description(),
                request.status(),
                request.priority()
        );
    }

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
