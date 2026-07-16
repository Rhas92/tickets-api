package com.example.tickets_api.exceptions;

import java.time.Instant;

/**
 * Consistent error body returned for every handled exception, so clients always
 * get the same shape regardless of what went wrong. Built by
 * {@code GlobalExceptionHandler}.
 *
 * @param status    the HTTP status code (e.g. 400, 404, 409)
 * @param message   a human-readable description of the problem
 * @param timestamp when the error was produced, as an absolute point in time (UTC)
 */
public record ErrorResponse(
		int status,
		String message,
		Instant timestamp
) {}