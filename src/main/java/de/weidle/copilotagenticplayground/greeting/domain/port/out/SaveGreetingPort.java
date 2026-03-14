package de.weidle.copilotagenticplayground.greeting.domain.port.out;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;

public interface SaveGreetingPort {

    void save(Greeting greeting);
}
