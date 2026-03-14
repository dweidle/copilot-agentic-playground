package de.weidle.copilotagenticplayground.greeting;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GreetingService greetingService;

    @Test
    void returnsGreetingPayload() throws Exception {
        given(greetingService.greet("Daniel")).willReturn(new GreetingResponse("Hello, Daniel!"));

        mockMvc.perform(get("/api/greeting").param("name", "Daniel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Hello, Daniel!"));
    }
}
