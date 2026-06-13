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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.tickets_api.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TicketControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        token = jwtService.generateToken("admin"); // "admin" is seeded by DataSeeder
    }

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Login broken"));
    }

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
}
