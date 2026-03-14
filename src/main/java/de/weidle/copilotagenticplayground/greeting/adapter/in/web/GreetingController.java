package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Greeting", description = "Greeting operations")
public class GreetingController {

    private final GreetUseCase greetUseCase;

    @GetMapping
    @Operation(
            summary = "Get a greeting",
            description =
                    "Returns a personalized greeting. Defaults to 'World' if no name is"
                            + " provided.")
    @ApiResponse(responseCode = "200", description = "Greeting returned successfully")
    public GreetingResponse greet(
            @Parameter(description = "Name to greet. Defaults to World if not provided.")
                    @RequestParam(required = false)
                    String name) {
        log.trace("GET /api/greeting called with name='{}'", name);
        Greeting greeting = greetUseCase.greet(name);
        GreetingResponse response = GreetingWebMapper.INSTANCE.toResponse(greeting);
        log.debug("Returning response: {}", response);
        return response;
    }
}
