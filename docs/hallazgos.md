# Findings

## SonarCloud — Quality Gate

### Bugs (22) — java:S6856

All bugs reported by SonarCloud were for rule **S6856**: `@PathVariable` parameters in the controller were not bound to template variables because the `@GetMapping`/`@PostMapping` annotations were only on the `StatisticsSwagger` interface, not on the `StatisticsController` implementation.

**Fix**: duplicate the mapping annotations on each controller method. Spring merges interface and implementation annotations, but SonarCloud does not follow the annotation inheritance chain.

**Commit**: `07eebcf`

### Vulnerabilities (7) — S7637 / S7636

- **5 × S7637** (use full commit SHA): GitHub Actions (`docker/*`, `azure/webapps-deploy`) used version tags (`@v3`, `@v5`, `@v6`) instead of full commit SHAs.
- **2 × S7636** (avoid expanding secrets in run block): `AZURE_WEBAPP_NAME` and `AZURE_WEBAPP_QA_NAME` secrets were expanded directly in a `run:` block instead of being passed as environment variables.

**Fix**: replaced version tags with fixed SHAs + version comments, moved secrets to `env:`.

**Commit**: `07eebcf`

---

## Tournament Service Integration

### `GET /tournaments/active` does not exist

The Statistics service needs to resolve "the active tournament" when `tournamentId` is not provided. It was designed expecting a `GET /tournaments/active` endpoint in Tournaments, but **it does not exist**. Tournaments exposes routes like `/tournaments/{id}/finalize` and `/tournaments/history`, but none that returns the currently active tournament.

**Risk**: if a client calls `GET /teams/{id}/statistics` without `tournamentId`, the service fails because it cannot resolve the active tournament.

### Recognition — stub without HTTP call

Tournaments has a `RecognitionAwardPort.triggerAwards()` hook invoked from `FinalizeTournamentService`, but its only implementation is a `LogRecognitionAwardAdapter` that just logs. The real HTTP call to `POST /tournaments/{id}/recognitions` from Statistics is never executed.

**Risk**: tournament recognition (top scorer, best defense) is never triggered automatically. Someone must call the endpoint manually.

### Wrong port

The Tournament service runs on port **8080**, not 8081 as initially assumed. Its routes also lack the `/api/v1` prefix.

---

## Architecture decisions

### PostgreSQL → MongoDB

The service was migrated from PostgreSQL/JPA to MongoDB to align with the TechCup ecosystem. SQL aggregations (`AVG`, `SUM`, `GROUP BY`) were replaced with Java streams over raw documents. This is a valid trade-off for a university tournament's data volume.

### IDs as String

IDs (`playerId`, `teamId`, `matchId`, `tournamentId`) are `String` because other microservices in the ecosystem use MongoDB `ObjectId`. This forces conversions at boundaries.

### RabbitMQ events

The service consumes Competition events via RabbitMQ when a match finishes. The integration is implemented but **not validated end-to-end** — Competition needs to send real events to confirm message format and deserialization work.
