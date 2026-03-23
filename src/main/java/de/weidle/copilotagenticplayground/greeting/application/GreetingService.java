package de.weidle.copilotagenticplayground.greeting.application;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.model.Language;
import de.weidle.copilotagenticplayground.greeting.domain.port.in.GreetUseCase;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GreetingService implements GreetUseCase {

    private final SaveGreetingPort saveGreetingPort;
    private final IntUnaryOperator randomIndex;

    @Autowired
    public GreetingService(SaveGreetingPort saveGreetingPort) {
        this(saveGreetingPort, bound -> ThreadLocalRandom.current().nextInt(bound));
    }

    GreetingService(SaveGreetingPort saveGreetingPort, IntUnaryOperator randomIndex) {
        this.saveGreetingPort = saveGreetingPort;
        this.randomIndex = randomIndex;
    }

    @Override
    public Greeting greet(String name) {
        log.trace("greet() called with name='{}'", name);
        String normalized = (name == null || name.isBlank()) ? "World" : name.trim();
        Language[] languages = Language.values();
        Language language = languages[randomIndex.applyAsInt(languages.length)];
        log.debug("Selected language: '{}' for name: '{}'", language, normalized);
        Greeting greeting =
                new Greeting(
                        normalized, language.getGreeting() + ", " + normalized + "! 👋", language);
        log.info("Greeting created for '{}' in '{}'", normalized, language);
        saveGreetingPort.save(greeting);
        return greeting;
    }
}
