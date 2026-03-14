package de.weidle.copilotagenticplayground.greeting.application;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import org.springframework.stereotype.Service;

@Service
public class GreetingService implements GreetUseCase {

    private final SaveGreetingPort saveGreetingPort;

    public GreetingService(SaveGreetingPort saveGreetingPort) {
        this.saveGreetingPort = saveGreetingPort;
    }

    @Override
    public Greeting greet(String name) {
        String normalized = (name == null || name.isBlank()) ? "World" : name.trim();
        Greeting greeting = new Greeting(normalized, "Hello, " + normalized + "!");
        saveGreetingPort.save(greeting);
        return greeting;
    }
}
