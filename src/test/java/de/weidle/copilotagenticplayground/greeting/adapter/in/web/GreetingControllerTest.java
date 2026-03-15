package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GreetUseCase greetUseCase;

    @Test
    void returnsGreetingPayload() throws Exception {
        given(greetUseCase.greet("Daniel")).willReturn(new Greeting("Daniel", "Hello, Daniel!"));

        mockMvc.perform(get("/api/greeting").param("name", "Daniel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Hello, Daniel!"));
    }
}
