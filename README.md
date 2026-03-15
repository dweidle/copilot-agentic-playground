# copilot-agentic-playground

A full-stack reference application for exploring GitHub Copilot's agentic capabilities, built with Spring Boot 4, React, and PostgreSQL using a hexagonal architecture.

![CI](https://github.com/dweidle/copilot-agentic-playground/actions/workflows/ci.yml/badge.svg)

---

## Features

- **Hexagonal architecture** — strict separation of domain, application, and adapter layers
- **Spring Boot 4 / Java 21** backend with REST API and OpenAPI (Swagger UI)
- **React + TypeScript** frontend (Vite) with a dev proxy to the backend
- **PostgreSQL 17** persistence via Docker Compose
- **Observability** — OpenTelemetry (OTLP export) + Spring Boot Actuator + Grafana LGTM stack
- **Testcontainers** for realistic database integration tests
- **Cucumber** for end-to-end acceptance tests
- **Spotless** for automated code formatting
- **CI/CD** — GitHub Actions for build/test on every PR and automated GitHub Releases on version tags

---

## Architecture

The project follows a **hexagonal (ports and adapters)** architecture. Dependencies always point inward — the domain knows nothing about Spring, JPA, or HTTP.

```
┌─────────────────────────────────────────────────┐
│                   Adapters (in)                 │
│          adapter/in/web/  (REST controllers)    │
└───────────────────┬─────────────────────────────┘
                    │ calls input ports
┌───────────────────▼─────────────────────────────┐
│              Application Layer                  │
│    application/  (@Service use-case impls)      │
└───────────────────┬─────────────────────────────┘
                    │ calls output ports
┌───────────────────▼─────────────────────────────┐
│                 Domain Layer                    │
│  domain/model/   (pure POJOs, no framework)     │
│  domain/port/in/ (input port interfaces)        │
│  domain/port/out/(output port interfaces)       │
└───────────────────┬─────────────────────────────┘
                    │ implemented by
┌───────────────────▼─────────────────────────────┐
│                Adapters (out)                   │
│   adapter/out/persistence/  (JPA adapters)      │
└─────────────────────────────────────────────────┘
```

| Package | Role |
|---|---|
| `domain/model/` | Pure domain objects — no framework annotations |
| `domain/port/in/` | Input port interfaces (use-case contracts) |
| `domain/port/out/` | Output port interfaces (persistence contracts) |
| `application/` | Use-case implementations (`@Service`) |
| `adapter/in/web/` | Inbound REST controllers |
| `adapter/out/persistence/` | JPA persistence adapters |

---

## Quick Start

### Prerequisites

| Tool | Minimum version |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| Docker + Docker Compose | any recent version |
| Node.js | 18+ |

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts **PostgreSQL 17** on port `5433` and the **Grafana LGTM** observability stack on port `3000`.

### 2. Start the backend

```bash
mvn spring-boot:run
```

The API is available at `http://localhost:8080`.  
Swagger UI is at `http://localhost:8080/swagger-ui.html`.

### 3. Start the frontend (optional)

```bash
cd frontend
npm install
npm run dev
```

The React app starts at `http://localhost:5173` and proxies API requests to the backend on `:8080`.

---

## API

| Method | Path | Description | Example response |
|---|---|---|---|
| `GET` | `/api/greeting?name={name}` | Returns a greeting and persists a log entry | `{"name":"Alice","message":"Hello, Alice!"}` |
| `GET` | `/swagger-ui.html` | Interactive OpenAPI documentation | — |
| `GET` | `/actuator/health` | Application health status | `{"status":"UP"}` |
| `GET` | `/actuator/info` | Application info | `{}` |
| `GET` | `/actuator/metrics` | Micrometer metrics | — |

### Example

```bash
curl "http://localhost:8080/api/greeting?name=Alice"
```

```json
{
  "name": "Alice",
  "message": "Hello, Alice!"
}
```

---

## Configuration

Key properties in `src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/playground` | PostgreSQL JDBC URL |
| `spring.datasource.username` | `playground` | Database username |
| `spring.datasource.password` | `playground` | Database password |
| `features.persistence.enabled` | `true` | Toggle greeting log persistence |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Swagger UI mount path |
| `management.endpoints.web.exposure.include` | `health,info,metrics,env,beans,loggers` | Exposed Actuator endpoints |
| `otel.service.name` | `copilot-agentic-playground` | Service name reported to OTel collector |
| `otel.metrics.exporter` | `none` | OTel metrics exporter (`none` disables metrics export) |

---

## Development

### Running tests

```bash
# All tests (Testcontainers auto-starts a PostgreSQL instance)
mvn test

# Single test class
mvn -Dtest=GreetingControllerTest test

# Cucumber end-to-end tests
mvn -Dtest=CucumberE2ETest test

# Full verify (compile + test + package)
mvn verify
```

> **Note:** Integration tests use Testcontainers. Docker must be running locally — no manual database setup required.

### Code formatting

The project uses **Spotless** to enforce consistent formatting. CI will fail on unformatted code.

```bash
# Check formatting
mvn spotless:check

# Auto-fix formatting
mvn spotless:apply
```

### Frontend

```bash
cd frontend

npm run dev        # Dev server with hot reload (proxies /api → :8080)
npm run build      # Production bundle (output: frontend/dist/)
npm run tsc        # TypeScript type-check without emitting files
```

---

## Observability

| Tool | URL | Notes |
|---|---|---|
| Grafana LGTM | `http://localhost:3000` | Dashboards, logs, traces, metrics |
| Spring Boot Actuator | `http://localhost:8080/actuator` | Health, info, metrics, loggers |
| Swagger UI | `http://localhost:8080/swagger-ui.html` | Live API exploration |

The backend exports telemetry via **OpenTelemetry OTLP** to the Grafana LGTM stack started by `docker compose up -d`. The service name is configured as `copilot-agentic-playground`.

---

## CI/CD

| Workflow | Trigger | What it does |
|---|---|---|
| `CI` | Push / PR to `main` | Runs `mvn verify` |
| `Release` | Push of a `v*` tag | Builds JAR, generates release notes, publishes a GitHub Release |

---

## Contributing

1. **Branch naming** — use a short, descriptive prefix: `feat/`, `fix/`, `docs/`, `chore/`
2. **Commit messages** — follow [Conventional Commits](https://www.conventionalcommits.org/):
   - `feat: add user registration endpoint`
   - `fix: handle empty name in greeting`
   - `docs: update configuration table`
3. **Pull requests** — open a PR against `main`; CI must pass before merging
4. **Formatting** — run `mvn spotless:apply` before pushing to avoid CI failures

---

## License

This project is intended as a learning and experimentation sandbox. See [LICENSE](LICENSE) if present, or treat it as unlicensed/personal use only.
