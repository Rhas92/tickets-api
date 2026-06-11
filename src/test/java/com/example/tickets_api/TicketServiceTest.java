package com.example.tickets_api;


import com.example.tickets_api.exceptions.TicketNotFoundException;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import com.example.tickets_api.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void shouldReturnAllTickets() {
        List<Ticket> mockTickets = List.of(
                new Ticket("Test #1", "This is the first test", Status.OPEN, Priority.HIGH),
                new Ticket("Test #2", "This is the second test", Status.OPEN, Priority.HIGH)
        );

        when(ticketRepository.findAll()).thenReturn(mockTickets);

        List<Ticket> result = ticketService.getTickets();

        assertEquals(2, result.size());
        assertEquals("Test #1", result.getFirst().getTitle());
    }

    @Test
    void shouldGetTicketById() {
        Ticket mockTicket = new Ticket("Test #1", "This is the first test", Status.OPEN, Priority.HIGH);
        when(ticketRepository.findById("1")).thenReturn(Optional.of(mockTicket));

        Ticket result = ticketService.getTicketById("1");
        assertEquals("Test #1", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        when(ticketRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(TicketNotFoundException.class , () -> ticketService.getTicketById("99"));
    }
}
