package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "features.persistence.enabled", havingValue = "false")
public class NoOpSaveGreetingAdapter implements SaveGreetingPort {

    @PostConstruct
    void logStartup() {
        log.warn(
                "Persistence is DISABLED (features.persistence.enabled=false) — greetings will"
                        + " NOT be stored");
    }

    @Override
    public void save(Greeting greeting) {
        log.trace("No-op save called for name='{}' — persistence disabled", greeting.name());
    }
}
