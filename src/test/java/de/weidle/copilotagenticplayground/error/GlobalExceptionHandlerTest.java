package de.weidle.copilotagenticplayground.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
        public String acceptBody(@RequestBody java.util.Map<String, String> body) {
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
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"));
    }

    @Test
    void malformedRequestBodyReturns400() throws Exception {
        mockMvc.perform(
                        post("/test/body")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void typeMismatchReturns400() throws Exception {
        mockMvc.perform(get("/test/typed").param("count", "notANumber"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid value for parameter 'count'"));
    }

    @Test
    void missingRequiredParameterReturns400() throws Exception {
        mockMvc.perform(get("/test/required"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required parameter 'name' is missing"));
    }

    @Test
    void notFoundReturns404() throws Exception {
        mockMvc.perform(get("/nonexistent/path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
