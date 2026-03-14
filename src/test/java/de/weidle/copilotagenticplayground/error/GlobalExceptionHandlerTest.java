package de.weidle.copilotagenticplayground.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestErrorController.class)
@Import(GlobalExceptionHandlerTest.TestErrorController.class)
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;

    @RestController
    static class TestErrorController {

        @GetMapping("/test/error")
        public String triggerError() {
            throw new RuntimeException("boom");
        }

        @PostMapping("/test/body")
        public String acceptBody(@RequestBody ErrorResponse body) {
            return "ok";
        }

        @GetMapping("/test/typed")
        public String typedParam(@RequestParam int count) {
            return "count=" + count;
        }

        @GetMapping("/test/required")
        public String requiredParam(@RequestParam String name) {
            return "name=" + name;
        }
    }

    @Test
    void unexpectedExceptionReturns500() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void malformedRequestBodyReturns400() throws Exception {
        mockMvc.perform(
                        post("/test/body")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed request body"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void typeMismatchReturns400() throws Exception {
        mockMvc.perform(get("/test/typed").param("count", "notANumber"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value for parameter 'count'"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void missingRequiredParameterReturns400() throws Exception {
        mockMvc.perform(get("/test/required"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Required parameter 'name' is missing"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void noHandlerFoundReturns404() throws Exception {
        mockMvc.perform(get("/nonexistent/path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
