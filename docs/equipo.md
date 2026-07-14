# Equipo

## Equipo GA — ga-statistics-service

El equipo **GA** es responsable del servicio del dominio **D4 — Estadísticas**.

<!-- TODO: completar con los nombres reales del equipo -->
| Nombre | Rol |
|---|---|
| _(completar)_ | Líder |
| _(completar)_ | Back-end |

---

## Organización del trabajo

El equipo usa **Scrum con sprints de una semana** y Jira como herramienta de seguimiento,
siguiendo la metodología definida para el proyecto TechCup Fútbol.

**Convenciones de ramas:**

```
main                        ← estable, versión entregable
develop                     ← rama de integración del equipo
feature/<nombre-de-tarea>   ← una rama por tarea, creada desde develop
```

Al terminar una tarea se abre un Pull Request de `feature/*` hacia `develop`.

## Contratos con otros equipos

Este servicio depende de, y es dependido por, otros dominios del sistema:

| Equipo / servicio | Relación |
|---|---|
| Competencia (`mk-competition-service`) | Envía eventos de partido vía `POST /events` |
| Torneos (`mk-tournament-service`) | Se le consulta el torneo activo; debe llamar `POST /tournaments/{id}/recognitions` al finalizar un torneo |

Ver el detalle de estos contratos, incluyendo lo que aún falta confirmar, en
[Arquitectura](arquitectura.md)