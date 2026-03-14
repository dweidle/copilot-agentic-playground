package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
@RequiredArgsConstructor
public class GreetingController {

    private final GreetUseCase greetUseCase;

    @GetMapping
    public GreetingResponse greet(@RequestParam(required = false) String name) {
        Greeting greeting = greetUseCase.greet(name);
        return GreetingWebMapper.INSTANCE.toResponse(greeting);
    }
}
