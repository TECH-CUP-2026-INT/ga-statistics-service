# Requerimientos

Los requerimientos funcionales asignados a este servicio, agrupados por dominio (D4 -
Estadísticas), todos implementados.

## Estadísticas de Torneo

| # | Requerimiento | Endpoint |
|---|---|---|
| 1 | Promedio de partidos G/E/P por equipo | `GET /teams/{id}/match-record` |
| 2 | Mayor goleador | `GET /rankings?type=GOALS` |
| 3 | Valla menos vencida (ranking de porteros) | `GET /goalkeeper-ranking` |
| 4 | Promedio de goles del equipo x partido | `GET /teams/{id}/average-goals` |
| 5 | Promedio de faltas x partido (equipo/global) | `GET /teams/{id}/average-fouls`, `GET /tournaments/{id}/match-averages` |
| 6 | Rankings públicos (posiciones, goleadores, juego limpio) | `GET /tournaments/{id}/standings`, `GET /rankings` |
| 7 | Estadísticas generales del torneo | `GET /tournaments/{id}/standings` |
| 8 | Enviar reconocimiento (empates) | `POST` / `GET /tournaments/{id}/recognitions` |

## Estadísticas de Jugador

| # | Requerimiento | Endpoint |
|---|---|---|
| 9 | Promedio de minutos jugados | `GET /players/{id}/average-minutes-played` |
| 10 | Faltas cometidas (total) | `GET /players/{id}/total-fouls` |
| 11 | Goles hechos (total) | `GET /players/{id}/total-goals` |
| 12 | Número de partidos jugados | `GET /players/{id}/matches-played` |
| 13 | Asistencias | `GET /players/{id}/assists` |
| 14 | Tarjetas | `GET /players/{id}/cards` |

## Estadísticas de Equipo

| # | Requerimiento | Endpoint |
|---|---|---|
| 15 | Estadísticas de equipo en torneo activo | `GET /teams/{id}/statistics` |
| 16 | Faltas del equipo (total) | `GET /teams/{id}/total-fouls` |
| 17 | Goles del equipo (favor/contra/diferencia) | `GET /teams/{id}/goals` |

## Estadísticas de Partido

| # | Requerimiento | Endpoint |
|---|---|---|
| 18 | Promedios del partido (goles, faltas, tarjetas) | `GET /tournaments/{id}/match-averages` |
| 19 | Tarjetas totales del partido/torneo | `GET /matches/{id}/cards`, `GET /tournaments/{id}/cards` |
| 20 | Estado del partido (G/E/P) + walkover | `GET /matches/{id}/result` |

!!! note "Sobre el walkover"
    No existe un valor especial para walkover en el modelo de datos. La regla de negocio
    es simple: cuando un equipo no se presenta, el equipo presente se registra como
    `WON` y el ausente como `LOST` — el mismo resultado que un partido jugado
    normalmente.
