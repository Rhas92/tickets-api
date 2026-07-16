package com.example.tickets_api.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Status} transitions.
 */
class StatusTest {

    /**
     * Tests for OPEN {@link Status} transitions.
     */
    @Test
    void openCanTransitionToClosed() {
        assertTrue(Status.OPEN.canTransitionTo(Status.CLOSED));
    }
    @Test
    void openCanTransitionToInProgress() {
        assertTrue(Status.OPEN.canTransitionTo(Status.IN_PROGRESS));
    }
    @Test
    void openCanTransitionToOpen() {
        assertTrue(Status.OPEN.canTransitionTo(Status.OPEN));
    }

    /**
     * Tests for IN_PROGRESS {@link Status} transitions.
     */
    @Test
    void inProgressCanTransitionToClosed() {
        assertTrue(Status.IN_PROGRESS.canTransitionTo(Status.CLOSED));
    }
    @Test
    void inProgressCanTransitionToInProgress() {
        assertTrue(Status.IN_PROGRESS.canTransitionTo(Status.IN_PROGRESS));
    }
    @Test
    void inProgressCannotTransitionToOpen() {
        assertFalse(Status.IN_PROGRESS.canTransitionTo(Status.OPEN));
    }

    /**
    * Tests for CLOSED {@link Status} transitions.
    */
    @Test
    void closedCanTransitionToClosed() {
        assertTrue(Status.CLOSED.canTransitionTo(Status.CLOSED));
    }
    @Test
    void closedCannotTransitionToOpen() {
        assertFalse(Status.CLOSED.canTransitionTo(Status.OPEN));
    }
    @Test
    void closedCannotTransitionToInProgress() {
        assertFalse(Status.CLOSED.canTransitionTo(Status.IN_PROGRESS));
    }
}
