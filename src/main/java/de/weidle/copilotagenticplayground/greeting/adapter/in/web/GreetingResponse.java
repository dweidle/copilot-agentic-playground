package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;

public class GreetingResponse {

    private final String message;

    @JsonCreator
    public GreetingResponse(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static GreetingResponse from(Greeting greeting) {
        return new GreetingResponse(greeting.getMessage());
    }
}
