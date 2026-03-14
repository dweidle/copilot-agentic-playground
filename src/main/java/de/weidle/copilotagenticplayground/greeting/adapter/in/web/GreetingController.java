package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    private final GreetUseCase greetUseCase;

    public GreetingController(GreetUseCase greetUseCase) {
        this.greetUseCase = greetUseCase;
    }

    @GetMapping
    public GreetingResponse greet(@RequestParam(required = false) String name) {
        Greeting greeting = greetUseCase.greet(name);
        return GreetingResponse.from(greeting);
    }
}
