package com.example.tickets_api.service;

import com.example.tickets_api.exceptions.TicketNotFoundException;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Page<Ticket> getTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
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
        log.info("Creating ticket with title '{}'", ticket.getTitle());
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Ticket ticket, String id) {
        ticket.setId(id);
        if (!ticketRepository.existsById(id)) {
            log.warn("Update failed - ticket {} not found", id);
            throw new TicketNotFoundException(id);
        }
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(String id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        log.info("Deleting ticket {}", id);
        ticketRepository.deleteById(id);
    }
}
