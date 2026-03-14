package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "features.persistence.enabled", havingValue = "false")
public class NoOpSaveGreetingAdapter implements SaveGreetingPort {

    @Override
    public void save(Greeting greeting) {
        // persistence disabled via feature flag — intentionally a no-op
    }
}
