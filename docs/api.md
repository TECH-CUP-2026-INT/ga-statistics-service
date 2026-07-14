# API REST

Base URL: `http://localhost:8085/api/v1/statistics`

Documentación interactiva (Swagger UI): `http://localhost:8085/swagger-ui/index.html`

!!! note "Filtro por torneo"
    Casi todos los endpoints de consulta aceptan `?tournamentId=` opcional. Si no se
    envía, la consulta es **histórica** (todos los torneos). La excepción es
    `/teams/{teamId}/statistics`, que resuelve el torneo activo internamente.

---

## Ingesta de eventos

Controlador: `StatisticsController`. Lo consume el servicio de Competencia.

### Registrar evento de partido

```http
POST /events
Content-Type: application/json
```

```json
{
  "playerId": "abc123",
  "teamId": "team-xyz",
  "matchId": "match-001",
  "tournamentId": "tournament-001",
  "result": "WON",
  "goals": 2,
  "assists": 1,
  "yellowCards": 1,
  "redCards": 0,
  "foulsCommitted": 3,
  "minutesPlayed": 90,
  "goalkeeper": false
}
```

| Código | Significado |
|---|---|
| `201` | Registrado correctamente |
| `400` | Error de validación |
| `409` | Ya existe un registro para ese jugador + partido |

`result` acepta `WON`, `DRAWN`, `LOST`. Un walkover se registra igual: el equipo presente
como `WON`, el ausente como `LOST`.

---

## Jugador — `/players/{playerId}`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/players/{id}/average-win-rate` | % de partidos ganados |
| `GET` | `/players/{id}/average-goals` | Promedio de goles por partido |
| `GET` | `/players/{id}/average-fouls` | Promedio de faltas por partido |
| `GET` | `/players/{id}/average-minutes-played` | Promedio de minutos jugados |
| `GET` | `/players/{id}/matches-played` | Total de partidos jugados |
| `GET` | `/players/{id}/total-goals` | Goles totales (no promedio) |
| `GET` | `/players/{id}/total-fouls` | Faltas totales |
| `GET` | `/players/{id}/assists` | Asistencias totales |
| `GET` | `/players/{id}/cards` | Tarjetas amarillas/rojas acumuladas |

---

## Equipo — `/teams/{teamId}`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/teams/{id}/statistics` | Estadísticas completas en el **torneo activo** |
| `GET` | `/teams/{id}/match-record` | G/E/P con porcentajes |
| `GET` | `/teams/{id}/average-goals` | Promedio de goles por partido |
| `GET` | `/teams/{id}/average-fouls` | Promedio de faltas por partido |
| `GET` | `/teams/{id}/total-fouls` | Faltas totales del equipo |
| `GET` | `/teams/{id}/goals` | Goles a favor, en contra y diferencia |

`/teams/{id}/statistics` puede devolver `502` si el servicio de Torneos no está
disponible o no reporta un torneo activo válido.

---

## Torneo — `/tournaments/{tournamentId}`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/tournaments/{id}/standings` | Tabla de posiciones completa |
| `GET` | `/rankings?type={TYPE}` | Ranking público: `GOALS`, `WINS`, `FOULS`, `MINUTES` |
| `GET` | `/goalkeeper-ranking` | Ranking de porteros (menos goles recibidos) |
| `GET` | `/tournaments/{id}/match-averages` | Promedios de goles/faltas/tarjetas por partido |
| `GET` | `/tournaments/{id}/cards` | Tarjetas totales del torneo |
| `POST` | `/tournaments/{id}/recognitions` | Genera y **guarda** el reconocimiento |
| `GET` | `/tournaments/{id}/recognitions` | Consulta el reconocimiento ya guardado |

### Generar reconocimiento

```http
POST /tournaments/{id}/recognitions
```

Calcula el máximo goleador y la mejor defensa, y los **guarda**. No recalcula en cada
consulta — quien quiera el resultado actualizado debe volver a llamar este `POST` (por
ejemplo, al finalizar el torneo). Si hay empate, **todos** los empatados quedan en la
respuesta.

```json
{
  "tournamentId": "tournament-001",
  "topScorers": [
    { "playerId": "p20", "goals": 8 },
    { "playerId": "p22", "goals": 8 }
  ],
  "topScorersGoals": 8,
  "bestDefenseTeams": [{ "teamId": "teamA", "goalsAgainst": 3 }],
  "bestDefenseGoalsAgainst": 3,
  "generatedAt": "2026-07-14T11:19:16.149"
}
```

`GET /tournaments/{id}/recognitions` devuelve `404` si aún no se ha generado.

---

## Partido — `/matches/{matchId}`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/matches/{id}/cards` | Tarjetas totales de ese partido |
| `GET` | `/matches/{id}/result` | Resultado por equipo (incluye walkover) |

---

## Manejo de errores

Todas las respuestas de error siguen el mismo formato:

```json
{
  "timestamp": "2026-07-09T08:03:32.23",
  "status": 409,
  "error": "Conflict",
  "messages": ["Ya existe una estadística registrada para el jugador abc123 en el partido match-001"],
  "path": "/api/v1/statistics/events"
}
```

| Situación | Status |
|---|---|
| Evento duplicado | `409` |
| Validación de campos | `400` |
| Reconocimiento aún no generado | `404` |
| Servicio de Torneos no disponible | `502` |
| Error inesperado | `500` |
