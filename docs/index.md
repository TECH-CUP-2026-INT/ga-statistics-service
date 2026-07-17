---
hide:
  - navigation
---

# Statistics Service — TechCup

<div class="grid cards" markdown>

-   **Statistics Service**

    Microservice responsible for calculating and exposing player, team, match, and tournament statistics: averages, totals, public rankings, and tournament recognitions.

-   **Spring Boot 3.5.6 + MongoDB**

    Built with Java 21 and MongoDB persistence. IDs (player, team, match, tournament) are `String` to remain compatible with MongoDB identifiers used across the TechCup ecosystem.

-   **97% test coverage**

    66 tests (service, controller, HTTP client) with JaCoCo — see [Testing](pruebas.md).

-   **Part of the TechCup ecosystem**

    A microservice within the DOSW platform that digitalizes the semester football tournament of the Escuela Colombiana de Ingeniería Julio Garavito.

</div>

---

## What does this service do?

`ga-statistics-service` centralizes all tournament statistics from the events sent by the Competition service when a match finishes:

- Computes **player averages and totals**: goals, fouls, minutes played, assists, cards.
- Computes **team statistics**: match record, goals for/against, standings.
- Generates **public rankings**: top scorers, fair play, best defense.
- Calculates and **persists tournament recognitions** (top scorer, best defense) upon tournament completion, handling ties.
- Exposes **per-match statistics**: result, cards, and averages.

---

## Repository

```
https://github.com/TECH-CUP-2026-INT/ga-statistics-service
```

## Quick start

```bash
# 1. Clone
git clone https://github.com/TECH-CUP-2026-INT/ga-statistics-service.git
cd ga-statistics-service

# 2. Start with Docker Compose (MongoDB included)
docker compose up --build

# 3. Service available at
http://localhost:8085

# 4. Interactive API docs (Swagger)
http://localhost:8085/swagger-ui/index.html
```

[See full configuration](configuracion.md){ .md-button .md-button--primary }
[See REST API](api.md){ .md-button }
