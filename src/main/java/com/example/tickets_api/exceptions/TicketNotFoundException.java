package com.example.tickets_api.exceptions;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String id) {
        super("Ticket not found with id: " + id);
    }
}
