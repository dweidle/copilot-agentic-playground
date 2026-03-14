package de.weidle.copilotagenticplayground.greeting;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private final GreetingLogRepository greetingLogRepository;

    public GreetingService(GreetingLogRepository greetingLogRepository) {
        this.greetingLogRepository = greetingLogRepository;
    }

    public GreetingResponse greet(String name) {
        String normalizedName = (name == null || name.isBlank()) ? "World" : name.trim();
        GreetingResponse response = new GreetingResponse("Hello, " + normalizedName + "!");
        greetingLogRepository.save(new GreetingLog(normalizedName, response.getMessage()));
        return response;
    }
}
