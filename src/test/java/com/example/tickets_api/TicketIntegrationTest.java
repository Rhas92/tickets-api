package com.example.tickets_api;

import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Data-integration tests for {@link TicketRepository} against a real MongoDB
 * started by Testcontainers. Verifies that persistence and the derived query
 * methods behave correctly end to end (no mocks at the data layer).
 */
@SpringBootTest
@Testcontainers
class TicketIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private TicketRepository ticketRepository;

    /** Clears the collection before each test so they stay independent. */
    @BeforeEach
    void cleanUp() {
        ticketRepository.deleteAll();
    }

    /** A saved ticket gets an id and can be read back. */
    @Test
    void shouldSaveAndRetrieveTicket() {
        Ticket ticket = new Ticket("Integration", "real DB test", Status.OPEN, Priority.HIGH);
        Ticket saved = ticketRepository.save(ticket);

        Optional<Ticket> found = ticketRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Integration", found.get().getTitle());
    }

    /** A deleted ticket can no longer be found. */
    @Test
    void shouldDeleteTicket() {
        Ticket ticket = new Ticket("Integration", "real DB test", Status.OPEN, Priority.HIGH);
        Ticket saved = ticketRepository.save(ticket);

        ticketRepository.deleteById(saved.getId());

        Optional<Ticket> found = ticketRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    /** The derived {@code findByStatus} returns only matching tickets. */
    @Test
    void shouldFindTicketsByStatus() {
        ticketRepository.save(new Ticket("A", "desc", Status.OPEN, Priority.HIGH));
        ticketRepository.save(new Ticket("B", "desc", Status.CLOSED, Priority.LOW));

        List<Ticket> openTickets = ticketRepository.findByStatus(Status.OPEN);

        assertEquals(1, openTickets.size());
        assertEquals("A", openTickets.getFirst().getTitle());
    }

    /** The derived {@code findByPriority} returns only matching tickets. */
    @Test
    void shouldFindTicketsByPriority() {
        ticketRepository.save(new Ticket("A", "desc", Status.OPEN, Priority.HIGH));
        ticketRepository.save(new Ticket("B", "desc", Status.CLOSED, Priority.LOW));

        List<Ticket> openTickets = ticketRepository.findByPriority(Priority.HIGH);

        assertEquals(1, openTickets.size());
        assertEquals("A", openTickets.getFirst().getTitle());
    }
}