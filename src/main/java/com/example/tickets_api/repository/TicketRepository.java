package com.example.tickets_api.repository;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Data access layer for {@link Ticket} documents.
 * <p>
 * Extends {@link MongoRepository} to inherit standard CRUD operations
 * (findAll, findById, save, deleteById). Custom finders are derived
 * automatically from their method names.
 */

public interface TicketRepository extends MongoRepository<Ticket, String> {

    /**
     * Finds all tickets that are in the given status.
     *
     * @param status the lifecycle state to filter by
     * @return the matching tickets (empty list if none)
     */
    List<Ticket> findByStatus(Status status);

    /**
     * Finds all tickets that have the given priority.
     *
     * @param priority the urgency level to filter by
     * @return the matching tickets (empty list if none)
     */
    List<Ticket> findByPriority(Priority priority);

    /**
     * Deletes the ticket with the given id in a single atomic operation.
     *
     * @param id the id of the deleted ticket.
     * @return the number of documents deleted (0 if none matched)
     */
    int deleteTicketById(String id);


}
