package com.example.tickets_api.service;


import com.example.tickets_api.exceptions.TicketNotFoundException;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

        when(ticketRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(mockTickets));

        Page<Ticket> result = ticketService.getTickets(Pageable.unpaged());

        assertEquals(2, result.getTotalElements());
        assertEquals("Test #1", result.getContent().getFirst().getTitle());
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
