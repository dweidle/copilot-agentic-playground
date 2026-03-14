package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "features.persistence.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class GreetingLogPersistenceAdapter implements SaveGreetingPort {

    private final GreetingLogJpaRepository repository;

    @Override
    public void save(Greeting greeting) {
        log.debug("Persisting greeting for name='{}'", greeting.name());
        repository.save(GreetingPersistenceMapper.INSTANCE.toEntity(greeting));
        log.debug("Greeting persisted successfully for name='{}'", greeting.name());
    }
}
