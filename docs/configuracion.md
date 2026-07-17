# Configuration

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8085` | HTTP server port |
| `MONGODB_URI` | `mongodb://localhost:27017/techcup_statistics` | MongoDB connection string |
| `TOURNAMENT_SERVICE_URL` | `http://localhost:8080` | Tournament service base URL |

## Running locally

### Prerequisites

- Java 21+
- MongoDB 7+ (or Docker)
- Docker (optional, for containerized MongoDB)

### With Docker Compose (recommended)

```bash
docker compose up --build
```

Starts both MongoDB and the service. The service is available at `http://localhost:8085`.

### Without Docker

```bash
# Start MongoDB separately, then:
./mvnw spring-boot:run
```

### Profile-specific configuration

No profiles are currently defined. All configuration is environment-variable driven for containerized deployments.
