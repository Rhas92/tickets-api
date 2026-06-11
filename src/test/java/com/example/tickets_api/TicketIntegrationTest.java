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

@SpringBootTest
@Testcontainers
class TicketIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void cleanUp() {
        ticketRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTicket() {
        Ticket ticket = new Ticket("Integration", "real DB test", Status.OPEN, Priority.HIGH);
        Ticket saved = ticketRepository.save(ticket);

        Optional<Ticket> found = ticketRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Integration", found.get().getTitle());
    }

    @Test
    void shouldDeleteTicket() {
        Ticket ticket = new Ticket("Integration", "real DB test", Status.OPEN, Priority.HIGH);
        Ticket saved = ticketRepository.save(ticket);

        ticketRepository.deleteById(saved.getId());

        Optional<Ticket> found = ticketRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindTicketsByStatus() {
        ticketRepository.save(new Ticket("A", "desc", Status.OPEN, Priority.HIGH));
        ticketRepository.save(new Ticket("B", "desc", Status.CLOSED, Priority.LOW));

        List<Ticket> openTickets = ticketRepository.findByStatus(Status.OPEN);

        assertEquals(1, openTickets.size());
        assertEquals("A", openTickets.getFirst().getTitle());
    }
    @Test
    void shouldFindTicketsByPriority() {
        ticketRepository.save(new Ticket("A", "desc", Status.OPEN, Priority.HIGH));
        ticketRepository.save(new Ticket("B", "desc", Status.CLOSED, Priority.LOW));

        List<Ticket> openTickets = ticketRepository.findByPriority(Priority.HIGH);

        assertEquals(1, openTickets.size());
        assertEquals("A", openTickets.getFirst().getTitle());
    }
}