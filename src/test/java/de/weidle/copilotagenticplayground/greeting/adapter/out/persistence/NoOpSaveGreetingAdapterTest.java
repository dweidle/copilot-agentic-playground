package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThatNoException;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import de.weidle.copilotagenticplayground.greeting.domain.model.Language;
import org.junit.jupiter.api.Test;

class NoOpSaveGreetingAdapterTest {

    private final NoOpSaveGreetingAdapter adapter = new NoOpSaveGreetingAdapter();

    @Test
    void saveDoesNothing() {
        assertThatNoException()
                .isThrownBy(
                        () -> adapter.save(new Greeting("Test", "Hello, Test!", Language.ENGLISH)));
    }
}
