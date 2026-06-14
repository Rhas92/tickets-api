package com.example.tickets_api.service;

import com.example.tickets_api.exceptions.TicketNotFoundException;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(String id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }
    public List<Ticket> getTicketsByStatus(Status status) {
        return ticketRepository.findByStatus(status);
    }
    public List<Ticket> getTicketsByPriority(Priority priority) {
        return ticketRepository.findByPriority(priority);
    }
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Ticket ticket, String id) {
        ticket.setId(id);
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(String id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        ticketRepository.deleteById(id);
    }
}
