# API

Base path: `/api/v1/statistics`

## Endpoints

### Register a match event

```
POST /events
```

Consumed by the Competition service when a match finishes. Each request contains the statistics of one player in one match.

### Player statistics

| Method | Path | Description |
|---|---|---|
| GET | `/players/{playerId}/average-win-rate` | Player's win rate |
| GET | `/players/{playerId}/average-goals` | Average goals per match |
| GET | `/players/{playerId}/average-fouls` | Average fouls per match |
| GET | `/players/{playerId}/average-minutes-played` | Average minutes per match |
| GET | `/players/{playerId}/matches-played` | Total matches played |
| GET | `/players/{playerId}/total-goals` | Total goals |
| GET | `/players/{playerId}/total-fouls` | Total fouls |
| GET | `/players/{playerId}/assists` | Total assists |
| GET | `/players/{playerId}/cards` | Yellow and red cards |

All accept an optional `?tournamentId` query parameter.

### Team statistics

| Method | Path | Description |
|---|---|---|
| GET | `/teams/{teamId}/statistics` | Overall team stats in a tournament |
| GET | `/teams/{teamId}/match-record` | Wins, draws, losses |
| GET | `/teams/{teamId}/average-goals` | Average goals per match |
| GET | `/teams/{teamId}/average-fouls` | Average fouls per match |
| GET | `/teams/{teamId}/total-fouls` | Total fouls |
| GET | `/teams/{teamId}/goals` | Goals for, against, difference |

All accept an optional `?tournamentId` query parameter.

### Tournament statistics

| Method | Path | Description |
|---|---|---|
| GET | `/tournaments/{tournamentId}/standings` | Tournament standings table |
| GET | `/tournaments/{tournamentId}/match-averages` | Per-match averages |
| GET | `/tournaments/{tournamentId}/cards` | Total cards |
| POST | `/tournaments/{tournamentId}/recognitions` | Generate recognitions |
| GET | `/tournaments/{tournamentId}/recognitions` | Get recognitions |

### Rankings

| Method | Path | Description |
|---|---|---|
| GET | `/rankings?type={GOALS,WINS,FOULS,MINUTES}` | Public player rankings |
| GET | `/goalkeeper-ranking` | Goalkeeper ranking |

### Match statistics

| Method | Path | Description |
|---|---|---|
| GET | `/matches/{matchId}/cards` | Match total cards |
| GET | `/matches/{matchId}/result` | Match result |

## Interactive documentation

When the service is running, visit:

```
http://localhost:8085/swagger-ui/index.html
```
