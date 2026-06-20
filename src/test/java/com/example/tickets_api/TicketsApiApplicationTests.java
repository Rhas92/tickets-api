package com.example.tickets_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Smoke test: verifies the full Spring application context starts successfully
 * (all beans wire together) with a real MongoDB from Testcontainers. If this
 * fails, something in the configuration is broken regardless of feature logic.
 */
@SpringBootTest
@Testcontainers
class TicketsApiApplicationTests {

	@Container
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

	/** Passes as long as the context loads without throwing. */
	@Test
	void contextLoads() {
	}
}
