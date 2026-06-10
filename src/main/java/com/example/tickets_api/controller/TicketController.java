package com.example.tickets_api.controller;

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
    public List<Ticket> getTickets() {
        return ticketService.getTickets();
    }
    @GetMapping("/tickets/status/{status}")
    public List<Ticket> getTicketsByStatus(@PathVariable Status status) {
        return ticketService.getTicketsByStatus(status);
    }
    @GetMapping("/tickets/{id}")
    public Ticket getTicketById(@PathVariable String id) {
        return ticketService.getTicketById(id);
    }
    @GetMapping("/tickets/priority/{priority}")
    public List<Ticket> getTicketsByPriority(@PathVariable Priority priority) {
        return ticketService.getTicketsByPriority(priority);
    }

    @PostMapping("/tickets")
    public Ticket createTicket(@Valid @RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }
    @PutMapping("/tickets/{id}")
    public Ticket updateTicket(@RequestBody Ticket ticket, @PathVariable String id) {
        return ticketService.updateTicket(ticket, id);
    }
    @DeleteMapping("/tickets/{id}")
    public String deleteTicket(@PathVariable String id){
        return ticketService.deleteTicket(id);
    }

}
