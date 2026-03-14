package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import org.springframework.stereotype.Component;

@Component
public class GreetingLogPersistenceAdapter implements SaveGreetingPort {

    private final GreetingLogJpaRepository repository;

    public GreetingLogPersistenceAdapter(GreetingLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Greeting greeting) {
        repository.save(new GreetingLogEntity(greeting.getName(), greeting.getMessage()));
    }
}
