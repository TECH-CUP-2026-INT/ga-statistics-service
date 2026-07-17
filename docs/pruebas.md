# Testing

## Summary

66 tests, 97% instruction coverage (JaCoCo), 80% branch coverage.

| Package | Coverage |
|---|---|
| `dto` | 100% |
| `controller` | 100% |
| `client` | 100% |
| `entity` | 100% |
| `service` | 97% |
| `exception` | 85% |

## Test organization

### `StatisticsServiceImplTest` (35 tests)

Unit tests for business logic using Mockito, mocking `PlayerMatchStatRepository`, `TournamentRecognitionRepository`, and `TournamentClient`. Covers: event registration (including duplicates and null values), all player averages and totals, team statistics, rankings (with and without tournament filter), tournament recognitions (including tie handling), and all match/tournament endpoints.

### `StatisticsControllerTest` (29 tests)

Controller tests with `@WebMvcTest` and `MockMvc`, mocking `StatisticsService`. Does not require MongoDB or the Tournament service running. Covers all 25 endpoints, plus error cases (`400` validation, `409` duplicate, `404` recognition not found, `502` when Tournaments is unavailable).

### `TournamentClientImplTest` (4 tests)

HTTP client tests mocking `RestClient` with Mockito (`Answers.RETURNS_DEEP_STUBS`) — no real network calls. Covers valid response, null response, null id, and connection failure.

### `ServiceStatisticsApplicationTests` (1 test)

Integration test that loads the full Spring context, including a real MongoDB connection (`contextLoads`).

## Running tests

```bash
mvn test
```

## Viewing coverage report

After running tests, open:

```
target/site/jacoco/index.html
```

## Note on the context test

`ServiceStatisticsApplicationTests` requires a real MongoDB instance (unlike the other tests which use mocks). If `MONGODB_URI` is not set, it defaults to `mongodb://localhost:27017/techcup_statistics`.
