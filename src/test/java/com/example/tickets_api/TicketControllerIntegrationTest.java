package com.example.tickets_api;

import com.example.tickets_api.model.AppUser;
import com.example.tickets_api.model.Priority;
import com.example.tickets_api.model.Status;
import com.example.tickets_api.model.Ticket;
import com.example.tickets_api.repository.TicketRepository;
import com.example.tickets_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private String adminToken;

    private String userToken;

    /**
     * Resets the collection and mints a fresh admin token before each test, so
     * tests stay independent and every request can authenticate.
     */
    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        if (userRepository.findByUsername("user").isEmpty()) {
            AppUser user = new AppUser(
                    "user",
                    passwordEncoder.encode("user123"),
                    "USER"
            );
            userRepository.save(user);
        }
        adminToken = jwtService.generateToken("admin"); // "admin" is seeded by DataSeeder
        userToken = jwtService.generateToken("user");
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
                        .header("Authorization", "Bearer " + adminToken)
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
                        .header("Authorization", "Bearer " + adminToken)
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
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("NOT_A_STATUS")));
    }

    /**
     * A ticket whose status is CLOSED cannot be reopened: CLOSED is terminal, so
     * the update is rejected with 409 Conflict rather than a misleading 404.
     * The ticket is seeded directly through the repository so the test fails only
     * for the behaviour under test, not for an unrelated POST failure.
     */
    @Test
    void shouldReturn409WhenTransitionIsInvalid() throws Exception {
        Ticket saved = ticketRepository.save(new Ticket("Status transition", "...", Status.CLOSED, Priority.HIGH));
        String id = saved.getId();
        String jsonTarget = """
                  {
                  "title": "Status transition",
                  "description": "...",
                  "status": "OPEN",
                  "priority": "HIGH"
                  }
                  """;

        mockMvc.perform(put("/tickets/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTarget))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot transition ticket from CLOSED to OPEN"));
    }

    /**
     * A ticket in OPEN can move forward to IN_PROGRESS. Guards the happy path: a
     * bug that rejected every transition would still satisfy the 409 test above,
     * so a valid transition must be asserted explicitly.
     */
    @Test
    void shouldUpdateTicketWhenTransitionIsValid() throws Exception {
        Ticket saved = ticketRepository.save(new Ticket("Status transition", "...", Status.OPEN, Priority.HIGH));
        String id = saved.getId();
        String jsonTarget = """
                {
                "title": "Status transition",
                "description": "...",
                "status": "IN_PROGRESS",
                "priority": "HIGH"
                }
                """;
        mockMvc.perform(put("/tickets/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTarget))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    /**
     * A request to a protected endpoint with no Authorization header is anonymous,
     * so it is rejected with 401 Unauthorized before reaching the controller. The
     * body is the standard ErrorResponse, confirming security errors share the same
     * shape as the rest of the API.
     */
    @Test
    void shouldReturn401WhenNoTokenIsProvided() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    /**
     * A DELETE is restricted to ADMINs. An authenticated non-admin user is
     * recognized (so this is not a 401) but not permitted, so the request is
     * rejected with 403 Forbidden.
     */
    @Test
    void shouldReturn403WhenUserIsUnauthorized() throws Exception {
        Ticket saved = ticketRepository.save(new Ticket("Ticket to delete", "...", Status.OPEN, Priority.HIGH));
        String id = saved.getId();
        mockMvc.perform(delete("/tickets/{id}", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    /**
     * The happy path for the ADMIN-only rule: an authenticated admin can delete a
     * ticket and gets 204 No Content. Pairs with the 403 test to prove the rule
     * both blocks non-admins and lets admins through, not just one or the other.
     */
    @Test
    void shouldDeleteTicketWhenAuthorizationIsValid() throws Exception {
        Ticket saved = ticketRepository.save(new Ticket("Ticket to delete", "...", Status.OPEN, Priority.HIGH));
        String id = saved.getId();

        mockMvc.perform(delete("/tickets/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
