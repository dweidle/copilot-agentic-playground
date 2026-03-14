package de.weidle.copilotagenticplayground.greeting.domain.port.in;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;

public interface GreetUseCase {

    Greeting greet(String name);
}
