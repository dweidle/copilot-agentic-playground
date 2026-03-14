package de.weidle.copilotagenticplayground.greeting.application;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GreetingService implements GreetUseCase {

    private final SaveGreetingPort saveGreetingPort;

    @Override
    public Greeting greet(String name) {
        log.trace("greet() called with name='{}'", name);
        String normalized = (name == null || name.isBlank()) ? "World" : name.trim();
        log.debug("Normalized name: '{}'", normalized);
        Greeting greeting = new Greeting(normalized, "Hello, " + normalized + "!");
        log.info("Greeting created for '{}'", normalized);
        saveGreetingPort.save(greeting);
        log.debug("Greeting persisted for '{}'", normalized);
        return greeting;
    }
}
