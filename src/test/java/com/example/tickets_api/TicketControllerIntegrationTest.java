package com.example.tickets_api;

import com.example.tickets_api.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.tickets_api.service.JwtService;

/**
 * Web-layer integration tests: real HTTP requests are driven through the whole
 * chain (security filters, controller, service, repository) with {@link MockMvc},
 * against a throwaway MongoDB started by Testcontainers. Because the endpoints are
 * secured, each request carries a real JWT minted in {@link #setUp()}.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TicketControllerIntegrationTest {

    /** Throwaway MongoDB container; {@code @ServiceConnection} wires Spring to it automatically. */
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private JwtService jwtService;

    private String token;

    /**
     * Resets the collection and mints a fresh admin token before each test, so
     * tests stay independent and every request can authenticate.
     */
    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        token = jwtService.generateToken("admin"); // "admin" is seeded by DataSeeder
    }

    /**
     * Happy path: a valid payload creates a ticket and returns 201 with the
     * persisted body (generated id and echoed title).
     */
    @Test
    void shouldCreateTicketViaHttp() throws Exception {
        String json = """
                {
                  "title": "Login broken",
                  "description": "Cannot log in",
                  "status": "OPEN",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Login broken"));
    }

    /**
     * Body validation: a blank title violates {@code @NotBlank} and must yield
     * 400 rather than persisting an invalid ticket.
     */
    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        String json = """
                {
                  "title": "",
                  "description": "Cannot log in",
                  "status": "OPEN",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Path-variable conversion: an unknown enum constant must be reported as a
     * client error (400), not 500. The request is authenticated on purpose —
     * without a token security would reject it with 401 before the conversion
     * ever happens, so the test would not exercise the handler under test.
     * Asserts both the status code and that the error body names the bad value.
     */
    @Test
    void shouldReturn400WhenStatusEnumIsInvalid() throws Exception {
        mockMvc.perform(get("/tickets/status/NOT_A_STATUS")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("NOT_A_STATUS")));
    }
}
