package io.github.rogeriocisi.airlab.platform;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The deploy workflow trusts {@code /actuator/health}: if it does not answer UP, it puts the
 * previous version back. On the first deploy it never did, because an empty
 * {@code SPRING_MAIL_HOST=} made Spring Boot build a sender for localhost:587 and its health
 * indicator reported DOWN. Mail is not part of the application's health, so the indicator is off
 * (application.yml), and this test runs with that same blank host.
 */
@SpringBootTest(properties = "spring.mail.host=")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthEndpointTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("sem SMTP de verdade, a saude responde UP: e o que o deploy confere")
    void health_is_up_without_a_reachable_smtp_server() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
