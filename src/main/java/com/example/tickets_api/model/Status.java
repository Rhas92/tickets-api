package com.example.tickets_api.model;

/**
 * Represents the lifecycle state of a support ticket.
 */

public enum Status {
    /** The ticket has been created and is waiting to be handled. */
    OPEN,
    /** The ticket is currently being worked on. */
    IN_PROGRESS,
    /** The ticket has been resolved and closed. */
    CLOSED
}
