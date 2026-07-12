package com.example.tickets_api.service;

import com.example.tickets_api.exceptions.TicketNotFoundException;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private final MongoTemplate mongoTemplate;

    public TicketService(TicketRepository ticketRepository, MongoTemplate mongoTemplate) {
        this.ticketRepository = ticketRepository;
        this.mongoTemplate = mongoTemplate;
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
     * Updates an existing ticket, locating and modifying the document in a single
     * atomic operation. If no ticket with the given id exists, nothing is created.
     * <p>
     * The id is only used to locate the document, never to write it, so the client
     * cannot move the record by changing the body.
     *
     * @param ticket the ticket to update
     * @param id the ticket's id
     * @return the updated ticket
     * @throws TicketNotFoundException if the ticket does not exist
     */
    public Ticket updateTicket(Ticket ticket, String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("title", ticket.getTitle())
                .set("description", ticket.getDescription())
                .set("status", ticket.getStatus())
                .set("priority", ticket.getPriority());
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Ticket updated = mongoTemplate.findAndModify(query, update, options, Ticket.class);
        if (updated == null) {
            log.warn("Update failed - ticket {} not found", id);
            throw new TicketNotFoundException(id);
        }
        return updated;
    }

    /**
     * Deletes the ticket with the given id in a single atomic operation: the
     * document is removed and the number of deleted documents tells us whether
     * it existed at all.
     *
     * @param id the ticket's id.
     * @throws TicketNotFoundException if the ticket does not exist
     */
    public void deleteTicket(String id) {
        int deletedCount = ticketRepository.deleteTicketById(id);
        if (deletedCount == 0) {
            log.warn("Deletion failed - ticket {} not found", id);
            throw new TicketNotFoundException(id);
        }
        log.info("Deleted ticket {}", id);
    }
}
