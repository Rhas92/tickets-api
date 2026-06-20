package com.example.tickets_api.exceptions;

/**
 * Thrown when an operation targets a ticket id that does not exist. Mapped to
 * 404 Not Found by {@code GlobalExceptionHandler}. Extends
 * {@link RuntimeException} so it is unchecked and needs no {@code throws} clause.
 *
 * @see com.example.tickets_api.exceptions.GlobalExceptionHandler
 */
public class TicketNotFoundException extends RuntimeException {

    /**
     * @param id the ticket id that could not be found; embedded in the message
     */
    public TicketNotFoundException(String id) {
        super("Ticket not found with id: " + id);
    }
}
