Feature: Greeting endpoint

  Scenario: Greeting a named user
    When the client requests greeting for "Daniel"
    Then the response status is 200
    And the greeting message is "Hello, Daniel! 👋"

  Scenario: Greeting falls back to the default user
    When the client requests the default greeting
    Then the response status is 200
    And the greeting message is "Hello, World! 👋"
