# Copilot Instructions

## Build, Test, and Lint Commands

- Start the app locally with `mvn spring-boot:run`
- Run the full test suite with `mvn test`
- Build the executable jar with `mvn clean package`
- Run a single test class with `mvn -Dtest=GreetingControllerTest test`
- Run a single test method with `mvn -Dtest=GreetingControllerTest#returnsGreetingPayload test`
- Run the Cucumber end-to-end suite with `mvn -Dtest=CucumberE2ETest test`
- Check formatting with `mvn spotless:check`
- Auto-fix formatting with `mvn spotless:apply`
- Start a local PostgreSQL with `docker compose up -d`

## High-Level Architecture

This repository is a single-module Maven Spring Boot application.

The application entry point is `de.weidle.copilotagenticplayground.CopilotAgenticPlaygroundApplication`. HTTP endpoints live under the `de.weidle.copilotagenticplayground` package tree and currently expose a small REST API at `/api/greeting`.

The current flow is controller-driven: `GreetingController` handles HTTP requests, delegates business logic to `GreetingService`, and returns a dedicated response DTO (`GreetingResponse`) that Spring serializes to JSON.

Greeting requests are persisted via Spring Data JPA. The `GreetingLog` entity records each greeting (name, message, timestamp) into a PostgreSQL database. Tests use Testcontainers to spin up an ephemeral PostgreSQL instance automatically.

End-to-end coverage lives in Cucumber feature files under `src/test/resources/features`. The Cucumber suite boots the Spring application on a random port and exercises the API over HTTP via step definitions in `src/test/java/de/weidle/copilotagenticplayground/e2e`.

## Key Conventions

- Keep production code under the root package `de.weidle.copilotagenticplayground` and group features by subpackage, as shown by the `greeting` feature package.
- Keep controllers thin and push response construction or business logic into Spring-managed services.
- Expose JSON APIs under `/api/...` paths.
- Prefer focused Spring tests that match the layer being changed: `@WebMvcTest` for controller behavior and `@SpringBootTest` for application wiring.
- Keep Cucumber scenarios high-level and HTTP-oriented; put bootstrapping in a dedicated Cucumber Spring configuration class and keep endpoint assertions in step definitions rather than mocking the web layer.
- Use Testcontainers with `@ServiceConnection` for database tests. Import `TestcontainersConfiguration` in any `@SpringBootTest` or Cucumber configuration class. The `@WebMvcTest` slice tests do not require a database.

## Git Workflow

- **Commit automatically** whenever a logical unit of work is complete (e.g. after adding a feature, fixing a bug, updating config, or refactoring). Do not batch unrelated changes into one commit.
- Use [Conventional Commits](https://www.conventionalcommits.org/) for every commit message:
  - `feat: add user endpoint` – new feature
  - `fix: handle null name in greeting service` – bug fix
  - `refactor: extract greeting logic into service` – code restructuring
  - `test: add e2e scenarios for greeting fallback` – test-only changes
  - `docs: update arc42 architecture documentation` – documentation
  - `chore: configure spotless formatting` – tooling, CI, dependencies
  - `build: upgrade spring boot to 3.4` – build system changes
- Use a scope when it adds clarity: `feat(greeting): add name validation`
- Keep the subject line lowercase, imperative, and under 72 characters.
- Add a body only when the "why" is not obvious from the subject.
- Run `mvn spotless:apply` before committing to ensure formatting is clean.

## CI/CD

- The `CI` workflow (`.github/workflows/ci.yml`) runs `mvn verify` on every push and PR to `main`. This includes Spotless formatting checks and all tests.
- The `Release` workflow (`.github/workflows/release.yml`) triggers on `v*` tags, sets the Maven version from the tag, builds the fat JAR, and publishes it as a GitHub Release.
- Dependabot is configured to keep Maven dependencies and GitHub Actions versions up to date weekly.
