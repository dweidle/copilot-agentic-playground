package de.weidle.copilotagenticplayground;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CopilotAgenticPlaygroundApplicationTests {

    @Test
    void contextLoads() {}
}
