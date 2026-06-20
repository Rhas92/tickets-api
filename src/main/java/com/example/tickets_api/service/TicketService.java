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

/**
 * Business logic for tickets. Sits between the controller and the repository,
 * translating "not found" situations into {@link TicketNotFoundException}
 * (mapped to 404 by the global handler).
 */
@Service
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /** Returns one page of tickets according to the given paging/sorting. */
    public Page<Ticket> getTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    /**
     * @param id the ticket id
     * @return the ticket
     * @throws TicketNotFoundException if no ticket has that id
     */
    public Ticket getTicketById(String id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    /** Returns all tickets in the given status. */
    public List<Ticket> getTicketsByStatus(Status status) {
        return ticketRepository.findByStatus(status);
    }

    /** Returns all tickets with the given priority. */
    public List<Ticket> getTicketsByPriority(Priority priority) {
        return ticketRepository.findByPriority(priority);
    }

    /** Persists a new ticket; MongoDB assigns its id on save. */
    public Ticket createTicket(Ticket ticket) {
        log.info("Creating ticket with title '{}'", ticket.getTitle());
        return ticketRepository.save(ticket);
    }

    /**
     * Replaces an existing ticket. The id from the path is forced onto the entity
     * so the client cannot move the record by changing the body.
     *
     * @throws TicketNotFoundException if the ticket does not exist
     */
    public Ticket updateTicket(Ticket ticket, String id) {
        ticket.setId(id);
        if (!ticketRepository.existsById(id)) {
            log.warn("Update failed - ticket {} not found", id);
            throw new TicketNotFoundException(id);
        }
        return ticketRepository.save(ticket);
    }

    /**
     * Deletes a ticket by id.
     *
     * @throws TicketNotFoundException if the ticket does not exist
     */
    public void deleteTicket(String id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        log.info("Deleting ticket {}", id);
        ticketRepository.deleteById(id);
    }
}
