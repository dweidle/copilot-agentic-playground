package de.weidle.copilotagenticplayground.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import de.weidle.copilotagenticplayground.greeting.adapter.in.web.GreetingResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

public class GreetingE2ESteps {

    @Autowired private TestRestTemplate restTemplate;

    @LocalServerPort private int port;

    private ResponseEntity<GreetingResponse> response;

    @When("the client requests greeting for {string}")
    public void theClientRequestsGreetingFor(String name) {
        response =
                restTemplate.getForEntity(
                        buildUrl("/api/greeting?name={name}"), GreetingResponse.class, name);
    }

    @When("the client requests the default greeting")
    public void theClientRequestsTheDefaultGreeting() {
        response = restTemplate.getForEntity(buildUrl("/api/greeting"), GreetingResponse.class);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int statusCode) {
        assertThat(response.getStatusCode().value()).isEqualTo(statusCode);
    }

    @Then("the greeting message is {string}")
    public void theGreetingMessageIs(String message) {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo(message);
    }

    private String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
