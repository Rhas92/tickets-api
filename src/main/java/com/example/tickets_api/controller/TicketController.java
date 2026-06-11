package com.example.tickets_api.controller;

import com.example.tickets_api.dto.TicketRequest;
import com.example.tickets_api.dto.TicketResponse;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TicketController {
    private final TicketService ticketService;

    public TicketController (TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public List<TicketResponse> getTickets() {
        return ticketService.getTickets().stream()
                .map(this::toResponse)
                .toList();
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
    public TicketResponse createTicket(@Valid @RequestBody TicketRequest request) {
        Ticket ticket = toEntity(request);
        return toResponse(ticketService.createTicket(ticket));
    }
    @PutMapping("/tickets/{id}")
    public TicketResponse updateTicket(@Valid @RequestBody TicketRequest request, @PathVariable String id) {
        Ticket ticket = toEntity(request);
        return toResponse(ticketService.updateTicket(ticket, id));
    }
    @DeleteMapping("/tickets/{id}")
    public String deleteTicket(@PathVariable String id){
        return ticketService.deleteTicket(id);
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
