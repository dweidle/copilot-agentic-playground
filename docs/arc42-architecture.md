# Architekturdokumentation – copilot-agentic-playground

> Basierend auf dem [arc42-Template](https://arc42.org) in minimalem Umfang.

---

## 1. Einführung und Ziele

### Aufgabenstellung

Das System ist eine Spring-Boot-Webanwendung, die als technischer Playground für agentengestützte Softwareentwicklung mit GitHub Copilot dient. Sie stellt eine kleine REST-API bereit, an der Architekturmuster, Teststrategien und Konventionen erprobt werden können.

### Qualitätsziele

| Priorität | Ziel | Beschreibung |
|-----------|------|--------------|
| 1 | Einfachheit | Minimale Komplexität, damit der Fokus auf dem Copilot-Workflow liegt. |
| 2 | Testbarkeit | Jede Schicht ist unabhängig und automatisiert testbar. |
| 3 | Erweiterbarkeit | Neue Features lassen sich als eigenständige Subpackages ergänzen, ohne bestehende Struktur zu ändern. |

### Stakeholder

| Rolle | Erwartung |
|-------|-----------|
| Entwickler | Verständliche Codebasis als Ausgangspunkt für Experimente. |
| KI-Agenten (Copilot) | Klare Konventionen und dokumentierte Befehle, um eigenständig Änderungen vorzunehmen. |

---

## 2. Randbedingungen

### Technische Randbedingungen

| Randbedingung | Erläuterung |
|---------------|-------------|
| Java 21 | Laufzeit- und Compile-Target (LTS). |
| Spring Boot 3.4.4 | Framework-Baseline; aktuelle stabile 3.4.x-Version. |
| Maven | Build-System; kein Gradle. |
| Embedded Tomcat | Servlet-Container wird über `spring-boot-starter-web` mitgeliefert. |
| PostgreSQL 17 | Relationale Datenbank für die Persistierung von Greeting-Anfragen. |
| Testcontainers | Stellt in Tests automatisch einen ephemeren PostgreSQL-Container bereit. |

### Organisatorische Randbedingungen

| Randbedingung | Erläuterung |
|---------------|-------------|
| Single-Modul-Projekt | Kein Multi-Modul-Maven-Build; alles in einer `pom.xml`. |
| Kein externer Datenspeicher | Kein Datenbank- oder Message-Broker-Einsatz. |

---

## 3. Kontextabgrenzung

### Fachlicher Kontext

```
┌──────────┐       HTTP GET /api/greeting        ┌──────────────────────────┐
│          │  ──────────────────────────────────▶  │                          │
│  Client  │                                      │  copilot-agentic-        │
│  (User / │  ◀──────────────────────────────────  │  playground              │
│  Agent)  │       JSON { "message": "..." }      │                          │
└──────────┘                                      └──────────────────────────┘
```

| Nachbar | Schnittstelle | Beschreibung |
|---------|---------------|--------------|
| Client (Browser, curl, Agent) | HTTP/JSON | Sendet GET-Requests an `/api/greeting` und empfängt JSON-Antworten. |
| PostgreSQL | JDBC/JPA | Persistiert jede Greeting-Anfrage (Name, Nachricht, Zeitstempel). |

### Technischer Kontext

Das System speichert Greeting-Anfragen in einer PostgreSQL-Datenbank. Für die lokale Entwicklung wird die Datenbank über Docker Compose bereitgestellt. In Tests wird ein ephemerer PostgreSQL-Container via Testcontainers gestartet.

---

## 4. Lösungsstrategie

| Entscheidung | Begründung |
|--------------|------------|
| Controller → Service → DTO | Klassische Schichtenarchitektur hält Controller dünn und isoliert Geschäftslogik im Service. |
| Feature-Packages | Jedes fachliche Feature (z. B. `greeting`) wird in einem eigenen Subpackage gebündelt (Controller, Service, DTO). |
| Immutable DTOs mit Jackson-Annotations | `GreetingResponse` ist `final`-feldbasiert; `@JsonCreator`/`@JsonProperty` ermöglichen sowohl Serialisierung als auch Deserialisierung. |
| Schichtspezifische Tests | `@WebMvcTest` für Controller, `@SpringBootTest` für Integration, Cucumber für E2E – jede Teststufe prüft genau ihre Ebene. |

---

## 5. Bausteinsicht

### Ebene 1 – System

```
copilot-agentic-playground
└── de.weidle.copilotagenticplayground
    ├── CopilotAgenticPlaygroundApplication   (Startpunkt)
    └── greeting                              (Feature-Package)
        ├── GreetingController                (REST-Schnittstelle)
        ├── GreetingService                   (Geschäftslogik)
        └── GreetingResponse                  (DTO)
```

### Ebene 2 – Greeting-Feature

```
┌──────────────────────────────────────────────────────┐
│                  greeting-Package                     │
│                                                      │
│  ┌────────────────────┐                              │
│  │ GreetingController │─────▶ GET /api/greeting      │
│  │  @RestController    │                              │
│  └────────┬───────────┘                              │
│           │ ruft auf (DI)                            │
│           ▼                                          │
│  ┌────────────────────┐     ┌───────────────────┐    │
│  │  GreetingService   │────▶│ GreetingResponse  │    │
│  │  @Service           │     │ (DTO, immutable)  │    │
│  └────────┬───────────┘     └───────────────────┘    │
│           │ persistiert                              │
│           ▼                                          │
│  ┌──────────────────────────┐  ┌─────────────────┐   │
│  │ GreetingLogRepository    │─▶│  GreetingLog    │   │
│  │ (Spring Data JPA)        │  │  (JPA-Entity)   │   │
│  └──────────────────────────┘  └─────────────────┘   │
│                                                      │
└──────────────────────────────────────────────────────┘
```

| Baustein | Verantwortung |
|----------|---------------|
| `GreetingController` | Nimmt HTTP-Requests entgegen, delegiert an den Service und gibt die Antwort als JSON zurück. |
| `GreetingService` | Enthält die Geschäftslogik: Normalisiert den Namensparameter und erzeugt die Grußnachricht. |
| `GreetingResponse` | Unveränderliches Transferobjekt mit einem `message`-Feld; Jackson-kompatibel. |
| `GreetingLogRepository` | Spring Data JPA Repository für den Datenbankzugriff. |
| `GreetingLog` | JPA-Entity, die eine Greeting-Anfrage in der `greeting_log`-Tabelle abbildet. |

---

## 6. Laufzeitsicht

### Szenario: Greeting mit Name

```
Client                Controller              Service                 Response
  │                       │                       │                       │
  │  GET /api/greeting    │                       │                       │
  │  ?name=Daniel         │                       │                       │
  │──────────────────────▶│                       │                       │
  │                       │  greet("Daniel")      │                       │
  │                       │──────────────────────▶│                       │
  │                       │                       │  new GreetingResponse  │
  │                       │                       │  ("Hello, Daniel!")    │
  │                       │                       │──────────────────────▶│
  │                       │  ◀─────────────────────────────────────────── │
  │  200 OK               │                       │                       │
  │  {"message":          │                       │                       │
  │   "Hello, Daniel!"}   │                       │                       │
  │◀──────────────────────│                       │                       │
```

### Szenario: Greeting ohne Name (Fallback)

Wird kein `name`-Parameter übergeben oder ist er leer, normalisiert `GreetingService` den Wert zu `"World"`. Die Antwort lautet dann `{"message": "Hello, World!"}`.

---

## 7. Verteilungssicht

Die Anwendung wird als einzelner Prozess betrieben. Es gibt keine verteilten Komponenten.

```
┌──────────────────────────────────────────────┐
│              Deployment-Knoten               │
│           (lokale JVM / Container)           │
│                                              │
│  ┌────────────────────────────────────────┐  │
│  │  Embedded Tomcat (Port 8080)           │  │
│  │  ┌──────────────────────────────────┐  │  │
│  │  │  Spring Application Context      │  │  │
│  │  │  ┌────────────┐ ┌─────────────┐  │  │  │
│  │  │  │ Controller │ │   Service   │  │  │  │
│  │  │  └────────────┘ └─────────────┘  │  │  │
│  │  └──────────────────────────────────┘  │  │
│  └────────────────────────────────────────┘  │
│                                              │
└──────────────────────────────────────────────┘
```

| Artefakt | Beschreibung |
|----------|--------------|
| `copilot-agentic-playground-0.0.1-SNAPSHOT.jar` | Ausführbares Fat-JAR mit eingebettetem Tomcat. Erzeugt über `mvn clean package`. |

### CI/CD-Pipeline

- **CI:** GitHub Actions Workflow führt `mvn verify` bei Push/PR auf `main` aus (Formatierung, Tests, Packaging).
- **CD:** Tag-getriggerter Workflow baut das JAR und erstellt ein GitHub Release.
- **Dependabot:** Wöchentliche automatische PRs für Maven- und Actions-Updates.

---

## 8. Querschnittliche Konzepte

### JSON-Serialisierung

Spring Boot konfiguriert Jackson automatisch. DTOs nutzen `@JsonCreator` / `@JsonProperty` für bidirektionale Kompatibilität (Serialisierung beim Response, Deserialisierung in Tests).

### Teststrategie

| Stufe | Werkzeug | Zweck |
|-------|----------|-------|
| Unit (Controller) | `@WebMvcTest` + MockMvc + Mockito | Isolierter Test der HTTP-Schicht ohne Service-Logik. |
| Integration | `@SpringBootTest` | Prüft, dass der gesamte Application-Context fehlerfrei hochfährt. |
| End-to-End | Cucumber + `@SpringBootTest(RANDOM_PORT)` + TestRestTemplate | Startet die Anwendung auf einem zufälligen Port und testet die API über echte HTTP-Aufrufe. |

### Dependency Injection

Constructor Injection wird gegenüber Field Injection bevorzugt (siehe `GreetingController`). Spring erkennt Beans durch Classpath-Scanning der `@SpringBootApplication`-Annotation.

### Continuous Integration / Continuous Delivery

| Aspekt | Werkzeug | Beschreibung |
|--------|----------|--------------|
| CI | GitHub Actions (`ci.yml`) | Automatischer Build und Test bei Push/PR auf `main`. |
| CD | GitHub Actions (`release.yml`) | Tag-basiertes Release mit Fat-JAR als GitHub-Release-Asset. |
| Dependency-Management | Dependabot | Wöchentliche PRs für veraltete Maven-Dependencies und Actions. |

---

## 9. Architekturentscheidungen

| ID | Entscheidung | Begründung |
|----|-------------|------------|
| ADR-01 | Spring Boot 3.4.4 mit Java 21 | Aktuelles LTS-Release für Framework und Laufzeit. |
| ADR-02 | Feature-Package-Struktur | Fachlich zusammengehörige Klassen (Controller, Service, DTO) liegen in einem Package, statt technisch nach Layern zu trennen. |
| ADR-03 | Cucumber für E2E statt reine Spring-Integrationstests | Gherkin-Szenarien dokumentieren das erwartete API-Verhalten in lesbarer Form und dienen gleichzeitig als ausführbare Spezifikation. |

---

## 10. Qualitätsanforderungen

### Qualitätsbaum

```
Qualität
├── Testbarkeit
│   ├── Jede Schicht einzeln testbar
│   └── E2E über echte HTTP-Aufrufe
├── Einfachheit
│   ├── Minimale Abhängigkeiten
│   └── Kein externer Infrastrukturbedarf
└── Erweiterbarkeit
    └── Neue Features als eigenständige Packages
```

### Qualitätsszenarien

| Szenario | Erwartetes Verhalten |
|----------|---------------------|
| Ein neues Feature (z. B. `/api/farewell`) wird hinzugefügt. | Es entsteht ein neues Subpackage mit Controller, Service und DTO, ohne bestehenden Code zu ändern. |
| Ein KI-Agent soll einen neuen Endpoint implementieren. | Die Copilot-Instructions und diese Dokumentation liefern genug Kontext, um Konventionen einzuhalten. |
| `mvn test` wird ausgeführt. | Alle Unit-, Integrations- und E2E-Tests laufen in unter 30 Sekunden durch. |

---

## 11. Risiken und technische Schulden

| Risiko / Schuld | Auswirkung | Maßnahme |
|-----------------|------------|----------|
| ~~Spring Boot 2.7 ist End-of-Life~~ | ~~Keine Sicherheitsupdates vom Projekt.~~ | Erledigt – Migration auf Spring Boot 3.4 und Java 21 ist abgeschlossen. |
| ~~Kein Fehlerhandling / `@ControllerAdvice`~~ | ~~Unbehandelte Exceptions erzeugen Spring-Default-Fehlerseiten.~~ | Erledigt – `GlobalExceptionHandler` liefert konsistente JSON-Fehlerantworten für 400, 404 und 500. |
| ~~Keine Persistenz~~ | ~~Zustand geht bei Neustart verloren.~~ | Erledigt – Greeting-Anfragen werden via JPA in PostgreSQL gespeichert. |

---

## 12. Glossar

| Begriff | Definition |
|---------|-----------|
| DTO | Data Transfer Object – einfaches Datenobjekt ohne Geschäftslogik, das zwischen Schichten ausgetauscht wird. |
| Feature-Package | Paket, das alle Klassen einer fachlichen Funktionalität bündelt (Controller, Service, DTO). |
| Fat-JAR | Ausführbares JAR-Archiv, das alle Abhängigkeiten einschließlich des eingebetteten Webservers enthält. |
| E2E | End-to-End – Teststufe, die das System von außen über seine öffentlichen Schnittstellen prüft. |
