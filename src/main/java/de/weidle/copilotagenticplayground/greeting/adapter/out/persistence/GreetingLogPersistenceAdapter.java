package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.port.out.SaveGreetingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GreetingLogPersistenceAdapter implements SaveGreetingPort {

    private final GreetingLogJpaRepository repository;

    @Override
    public void save(Greeting greeting) {
        repository.save(GreetingPersistenceMapper.INSTANCE.toEntity(greeting));
    }
}
