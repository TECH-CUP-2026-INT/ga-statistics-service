# ga-statistics-service

Microservicio de **Estadísticas** de TECHCUP FÚTBOL. Centraliza el cálculo y la consulta
de estadísticas de jugadores a partir de los eventos generados durante los partidos
(goles, faltas, tarjetas, minutos jugados y resultado).

Forma parte de la arquitectura de microservicios del proyecto.

## Stack

- Java 21
- Spring Boot 3.5.6 (Web, Data JPA, Validation)
- PostgreSQL
- Lombok
- JUnit 5 + Mockito + AssertJ (tests)

## Arquitectura del servicio

El dato atómico es `PlayerMatchStat`: el desempeño de **un jugador** en **un partido**.
A partir de esa tabla se calculan todos los promedios y rankings con consultas de
agregación (`AVG`, `COUNT`, `SUM`), sin necesidad de traer los registros a memoria.

```
controller/   -> Endpoints REST
service/      -> Lógica de negocio (promedios, rankings, validaciones)
repository/   -> Acceso a datos y queries de agregación (JPA)
entity/       -> Modelo persistente (PlayerMatchStat, MatchResult)
dto/          -> Contratos de entrada/salida (requests y responses)
exception/    -> Manejo centralizado de errores
```

## Cómo levantarlo localmente

### 1. Base de datos

Necesitas una instancia de PostgreSQL corriendo, con una base de datos llamada
`techcup_statistics` (puedes crearla con pgAdmin o `psql`).

### 2. Variables de entorno

El servicio lee la configuración de conexión desde variables de entorno (con valores
por defecto si no las defines):

| Variable      | Default                                              | Descripción                  |
|---------------|-------------------------------------------------------|-------------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/techcup_statistics` | URL de conexión JDBC          |
| `DB_USERNAME` | `postgres`                                            | Usuario de PostgreSQL         |
| `DB_PASSWORD` | `postgres`                                            | Contraseña de PostgreSQL      |
| `SERVER_PORT` | `8085`                                                | Puerto en el que corre la app |

### 3. Correrlo

```bash
# Windows (CMD)
set DB_PASSWORD=tu_contraseña
mvnw spring-boot:run

```

Al iniciar, Hibernate crea automáticamente la tabla `player_match_stats` (modo
`ddl-auto=update`, pensado para desarrollo).

## Correr los tests

```bash
mvnw test
```

> Nota: el test `ServiceStatisticsApplicationTests` levanta el contexto completo de
> Spring y necesita conexión real a la base de datos, así que exporta `DB_PASSWORD`
> antes de correr los tests igual que al levantar la app.

## Endpoints

Base path: `/api/v1/statistics`

### Ingesta de eventos (interno — lo consume el servicio de Competencia)

**`POST /events`**

Se llama una vez finaliza un partido, con el resumen de **un jugador** en ese partido.

Request body:
```json
{
  "playerId": 1,
  "teamId": 10,
  "matchId": 100,
  "tournamentId": 1,
  "result": "WON",
  "goals": 2,
  "yellowCards": 1,
  "redCards": 0,
  "foulsCommitted": 3,
  "minutesPlayed": 90
}
```
- `result` acepta: `WON`, `DRAWN`, `LOST`.
- Los campos numéricos (`goals`, `yellowCards`, `redCards`, `foulsCommitted`,
  `minutesPlayed`) son opcionales; si no se envían, se guardan como `0`.
- Un mismo `playerId` + `matchId` **no puede registrarse dos veces**: si Competencia
  reintenta el envío, el servicio responde `409 Conflict`.

Respuestas: `201 Created` | `400 Bad Request` (validación) | `409 Conflict` (duplicado)

### Consultas públicas

Todos los endpoints de consulta aceptan un parámetro opcional `tournamentId`. Si no se
envía, la consulta es **histórica** (todos los torneos del jugador).

| Método | Endpoint                                            | Descripción                                    |
|--------|------------------------------------------------------|-------------------------------------------------|
| GET    | `/players/{playerId}/average-win-rate`               | % de partidos ganados                           |
| GET    | `/players/{playerId}/average-goals`                  | Promedio de goles por partido                   |
| GET    | `/players/{playerId}/average-fouls`                  | Promedio de faltas cometidas por partido        |
| GET    | `/players/{playerId}/average-minutes-played`         | Promedio de minutos jugados por partido         |
| GET    | `/players/{playerId}/matches-played`                 | Número total de partidos jugados                |
| GET    | `/rankings`                                          | Ranking público (ver detalle abajo)             |

Ejemplo de respuesta (`average-goals`):
```json
{
  "playerId": 1,
  "tournamentId": null,
  "metric": "averageGoals",
  "value": 1.67,
  "matchesConsidered": 3
}
```

Ejemplo de respuesta (`matches-played`):
```json
{
  "playerId": 1,
  "tournamentId": null,
  "matchesPlayed": 5
}
```

#### Rankings

**`GET /rankings?type={TYPE}&tournamentId={id}&limit={n}`**

| Parámetro      | Obligatorio | Default     | Descripción                                          |
|----------------|-------------|-------------|-------------------------------------------------------|
| `type`         | Sí          | —           | `GOALS`, `WINS`, `FOULS` o `MINUTES`                   |
| `tournamentId` | No          | (histórico) | Filtra el ranking a un torneo específico               |
| `limit`        | No          | `10`        | Cantidad de jugadores a devolver (Top N)                |

- `GOALS`: más goles primero (botín de oro).
- `WINS`: más partidos ganados primero.
- `FOULS`: **menos** faltas primero (tabla de juego limpio).
- `MINUTES`: más minutos acumulados primero.

Ejemplo:
```
GET /api/v1/statistics/rankings?type=GOALS&limit=5
```
```json
{
  "type": "GOALS",
  "tournamentId": null,
  "entries": [
    { "position": 1, "playerId": 1, "value": 8 },
    { "position": 2, "playerId": 7, "value": 6 }
  ]
}
```

> Este servicio solo conoce el `playerId`. El nombre, foto y equipo del jugador los
> enriquece el frontend (o el orquestador) consultando el servicio de Usuarios y
> Jugadores — cada microservicio es dueño únicamente de su propio dominio.
> Este servicio solo conoce el `playerId`. El nombre, foto y equipo del jugador los
> enriquece el frontend (o el orquestador) consultando el servicio de Usuarios y
> Jugadores — cada microservicio es dueño únicamente de su propio dominio.


## Manejo de errores

Todas las respuestas de error siguen el mismo formato:
```json
{
  "timestamp": "2026-07-09T08:03:32.23",
  "status": 409,
  "error": "Conflict",
  "messages": ["Ya existe una estadística registrada para el jugador 1 en el partido 100"],
  "path": "/api/v1/statistics/events"
}
```

## Flujo de ramas del equipo

Este repositorio sigue un flujo simplificado tipo Gitflow:

- `main`: siempre estable, versión entregable.
- `develop`: rama de integración del equipo.
- `feature/<nombre>`: una rama por tarea, creada desde `develop`. Al terminar, se abre
  un Pull Request hacia `develop`.