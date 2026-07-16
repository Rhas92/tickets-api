package com.example.tickets_api.exceptions;

import com.example.tickets_api.model.Status;

/**
 * Thrown when a ticket exists but its current status does not allow moving to
 * the requested one. Maps to HTTP 409 Conflict.
 */

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(Status current, Status target) {
        super("Cannot transition ticket from " + current + " to " + target);
    }
}
