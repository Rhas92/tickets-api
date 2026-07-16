package com.example.tickets_api.model;

/**
 * Represents the lifecycle state of a support ticket.
 */

public enum Status {
    /** The ticket has been created and is waiting to be handled. */
    OPEN(0),
    /** The ticket is currently being worked on. */
    IN_PROGRESS(1),
    /** The ticket has been resolved and closed. */
    CLOSED(2);

    private final int order;

    Status(int order) {
        this.order = order;
    }
    /**
     * Whether a ticket in this status may move to the given status.
     * <p>
     * The lifecycle only moves forward or stays put: a ticket can be closed
     * without being worked on, but it can never go back. {@code CLOSED} is
     * terminal — if the problem reappears, a new ticket is opened.
     *
     * @param target the status the ticket would move to
     * @return true if the transition is allowed
     */
    public boolean canTransitionTo(Status target) {
        return target.order >= this.order;
    }
}
