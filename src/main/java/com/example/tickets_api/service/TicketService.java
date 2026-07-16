package com.example.tickets_api.service;

import com.example.tickets_api.exceptions.InvalidStatusTransitionException;
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

import java.util.Arrays;
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

    /** Returns one page of tickets in the given status. */
    public Page<Ticket> getTicketsByStatus(Status status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable);
    }

    /** Returns one page of tickets with the given priority. */
    public Page<Ticket> getTicketsByPriority(Priority priority, Pageable pageable) {
        return ticketRepository.findByPriority(priority, pageable);
    }

    /**
     * Persists a new ticket; MongoDB assigns its id on save.
     *
     * The title is sanitized before being logged, but stored verbatim.
     * @param ticket the ticket to create
     * @return the created ticket
     * */
    public Ticket createTicket(Ticket ticket) {
        String safeTitle = sanitizeForLog(ticket.getTitle());
        log.info("Creating ticket with title '{}'", safeTitle);
        return ticketRepository.save(ticket);
    }

    /**
     * Updates an existing ticket, locating and modifying the document in a single
     * atomic operation. If no ticket with the given id exists, nothing is created.
     * <p>
     * The status transition is enforced inside the query itself: the update only
     * matches a document whose current status allows moving to the requested one
     * (see {@link Status#canTransitionTo}). A ticket's lifecycle only moves forward
     * or stays put, and {@code CLOSED} is terminal.
     * <p>
     * The id is only used to locate the document, never to write it, so the client
     * cannot move the record by changing the body. It is sanitized before being
     * logged.
     *
     * @param ticket the ticket to update
     * @param id the ticket's id
     * @return the updated ticket
     * @throws TicketNotFoundException if the ticket does not exist
     * @throws InvalidStatusTransitionException if the ticket exists but the
     *                                          transition is invalid.
     */
    public Ticket updateTicket(Ticket ticket, String id) {
        Status target = ticket.getStatus();
        List<Status> allowedSources = Arrays.stream(Status.values())
                .filter(s -> s.canTransitionTo(target))
                .toList();

        String safeId = sanitizeForLog(id);
        Query query = new Query(Criteria.where("_id").is(id).and("status").in(allowedSources));
        Update update = new Update()
                .set("title", ticket.getTitle())
                .set("description", ticket.getDescription())
                .set("status", ticket.getStatus())
                .set("priority", ticket.getPriority());
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Ticket updated = mongoTemplate.findAndModify(query, update, options, Ticket.class);
        if (updated == null) {
            Ticket exists = ticketRepository.findById(id).orElse(null);
            if (exists != null) {
                log.warn("Update failed - status {} update to {} is invalid", exists.getStatus(), target);
                throw new InvalidStatusTransitionException(exists.getStatus(), target);
            }
            log.warn("Update failed - ticket {} not found", safeId);
            throw new TicketNotFoundException(id);
        }
        return updated;
    }

    /**
     * Deletes the ticket with the given id in a single atomic operation: the
     * document is removed and the number of deleted documents tells us whether
     * it existed at all. The id is sanitized before being logged.
     *
     * @param id the ticket's id.
     * @throws TicketNotFoundException if the ticket does not exist
     */
    public void deleteTicket(String id) {
        String safeId = sanitizeForLog(id);
        int deletedCount = ticketRepository.deleteTicketById(id);
        if (deletedCount == 0) {
            log.warn("Deletion failed - ticket {} not found", safeId);
            throw new TicketNotFoundException(id);
        }
        log.info("Deleted ticket {}", safeId);
    }

    /**
     * Strips control characters from a user-supplied id before it is logged,
     * so a crafted value cannot forge new log entries (CWE-117).
     */
    private String sanitizeForLog(String string) {
        return string.replaceAll("\\p{Cntrl}", "_");
    }
}
