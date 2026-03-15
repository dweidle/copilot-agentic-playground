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
- **Frontend** (in `frontend/`):
  - Install dependencies: `npm install`
  - Start dev server (with proxy to backend): `npm run dev`
  - Build production bundle: `npm run build`
  - Type-check: `npm run tsc`

## High-Level Architecture

This repository is a single-module Maven Spring Boot application that follows **hexagonal architecture** (ports and adapters).

The application entry point is `de.weidle.copilotagenticplayground.CopilotAgenticPlaygroundApplication`. Each feature (e.g. `greeting`) is organized into three subpackages:

- **`domain/`** – Pure domain model (`Greeting`) and port interfaces. The domain layer has zero framework dependencies.
  - `model/` – Domain objects (POJOs, no Spring/JPA annotations).
  - `port/in/` – Input port interfaces (e.g. `GreetUseCase`) that define what the application can do.
  - `port/out/` – Output port interfaces (e.g. `SaveGreetingPort`) that define what the application needs from infrastructure.
- **`application/`** – Use-case implementations (e.g. `GreetingService`) annotated with `@Service`. They implement input ports and depend on output ports.
- **`adapter/`** – Technical adapters that connect the outside world to the domain.
  - `in/web/` – Inbound web adapter (`GreetingController`, `GreetingResponse` DTO).
  - `out/persistence/` – Outbound persistence adapter (`GreetingLogPersistenceAdapter`, `GreetingLogEntity`, `GreetingLogJpaRepository`).

Adapters depend on ports, never the other way around. The dependency direction always points inward: adapters → ports → domain.

The greeting flow: HTTP request → `GreetingController` (web adapter) → `GreetUseCase` (input port) → `GreetingService` (application) → `SaveGreetingPort` (output port) → `GreetingLogPersistenceAdapter` (persistence adapter) → PostgreSQL. Tests use Testcontainers to spin up an ephemeral PostgreSQL instance automatically.

A **React + TypeScript frontend** (`frontend/`) built with Vite communicates with the backend via `GET /api/greeting?name=`. During development the Vite dev server proxies `/api/*` to `http://localhost:8080`.

End-to-end coverage lives in Cucumber feature files under `src/test/resources/features`. The Cucumber suite boots the Spring application on a random port and exercises the API over HTTP via step definitions in `src/test/java/de/weidle/copilotagenticplayground/e2e`.

## Key Conventions

- Organize each feature into `domain/`, `application/`, and `adapter/` subpackages under the root package `de.weidle.copilotagenticplayground`, as shown by the `greeting` feature package.
- Keep the domain layer free of Spring, JPA, and other framework annotations.
- Controllers and persistence adapters depend on port interfaces, not on application services directly.
- Keep controllers thin and push response construction or business logic into application services.
- Expose JSON APIs under `/api/...` paths.
- Prefer focused Spring tests that match the layer being changed: `@WebMvcTest` for controller behavior and `@SpringBootTest` for application wiring.
- Keep Cucumber scenarios high-level and HTTP-oriented; put bootstrapping in a dedicated Cucumber Spring configuration class and keep endpoint assertions in step definitions rather than mocking the web layer.
- Use Testcontainers with `@ServiceConnection` for database tests. Import `TestcontainersConfiguration` in any `@SpringBootTest` or Cucumber configuration class. The `@WebMvcTest` slice tests do not require a database.

## Issue Workflow

Every GitHub issue is implemented on a dedicated feature branch, reviewed via a Pull Request, and summarised back in the issue itself.

### Step-by-step

1. **Create a feature branch** before touching any code:
   ```
   git checkout -b feat/issue-{N}-{short-slug}
   ```
   Example: `feat/issue-9-release-notes`

2. **Implement** the issue on that branch following all coding conventions below.

3. **Commit** with `Closes #N` in the commit body so GitHub links the PR automatically:
   ```
   feat(scope): short subject

   Longer description of what was done and why.

   Closes #N

   Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
   ```

4. **Push** the branch and open a PR:
   ```bash
   git push -u origin feat/issue-{N}-{short-slug}
   bash scripts/open-issue-pr.sh {N} "PR title" "PR body"
   ```
   `scripts/open-issue-pr.sh` requires the `gh` CLI to be installed and authenticated (`gh auth login`).

5. **Post a summary comment** on the issue using `scripts/open-issue-pr.sh` (the script does this automatically after opening the PR).

### Branch naming

| Type | Pattern | Example |
|------|---------|---------|
| Feature / story | `feat/issue-{N}-{slug}` | `feat/issue-9-release-notes` |
| Bug fix | `fix/issue-{N}-{slug}` | `fix/issue-12-null-greeting` |
| Chore / docs | `chore/issue-{N}-{slug}` | `chore/issue-7-update-readme` |

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

## PR Health Checks

When checking the status of open PRs, **always check both CI and merge conflicts** together:

```bash
gh pr list --json number,title,mergeable,mergeStateStatus,statusCheckRollup \
  | python3 -c "
import sys, json
prs = json.load(sys.stdin)
for pr in sorted(prs, key=lambda p: p['number']):
    checks = pr.get('statusCheckRollup', [])
    ci = checks[0]['conclusion'] if checks else 'pending'
    ci_icon = '✅' if ci == 'SUCCESS' else ('🔄' if not checks else '❌')
    mergeable = pr.get('mergeable', 'UNKNOWN')
    merge_icon = '⚠️ CONFLICT' if mergeable == 'CONFLICTING' else ('✅' if mergeable == 'MERGEABLE' else '❓')
    print(f'PR #{pr[\"number\"]}: CI={ci_icon} | merge={merge_icon} | {pr[\"title\"][:60]}')
"
```

### Resolving merge conflicts

When a PR shows `mergeable=CONFLICTING`:

1. Determine what the PR's **unique net change** is vs. current `main` (use `git diff origin/main...origin/<branch>`)
2. Check if any of those changes are **already in main** — if so, the PR may be **superseded** and should be closed with `gh pr close <N> --comment "Superseded by ..."`)
3. If the PR still has value, create a fresh branch from `main`, apply only the unique changes, run `mvn --batch-mode verify` locally (tests must pass), then force-push to the PR branch
