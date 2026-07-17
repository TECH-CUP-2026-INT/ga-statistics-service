# Hallazgos

## SonarCloud — Quality Gate

### Bugs (22) — java:S6856

Todos los bugs reportados por SonarCloud correspondían a la regla **S6856**: los parámetros
`@PathVariable` del controller no se vinculaban a las variables de template porque los
`@GetMapping`/`@PostMapping` estaban solo en la interfaz `StatisticsSwagger`, no en la
implementación `StatisticsController`.

**Solución**: duplicar las anotaciones de mapeo en cada método del controller. Spring
merges las anotaciones de interfaz e implementación, pero SonarCloud no sigue la cadena
de herencia de anotaciones.

**Commit**: `07eebcf`

### Vulnerabilidades (7) — S7637 / S7636

- **5 × S7637** (use full commit SHA): las actions de GitHub (`docker/*`, `azure/webapps-deploy`)
  usaban version tags (`@v3`, `@v5`, `@v6`) en lugar de SHA completo.
- **2 × S7636** (avoid expanding secrets in run block): los secrets
  `AZURE_WEBAPP_NAME` y `AZURE_WEBAPP_QA_NAME` se expandían directamente en el `run:`
  block en lugar de pasarse como variables de entorno.

**Solución**: reemplazar version tags por SHA fijo + comentario con versión, y mover
secrets a `env:`.

**Commit**: `07eebcf`

---

## Integración con Torneos

### `GET /tournaments/active` no existe

El servicio de Estadísticas necesita resolver "el torneo activo" cuando no se pasa
`tournamentId`. Se diseñó esperando un endpoint `GET /tournaments/active` en Torneos,
pero **no existe**. Torneos expone rutas como `/tournaments/{id}/finalize` y
`/tournaments/history`, pero ninguna que devuelva el torneo activo actual.

**Riesgo**: si un cliente llama a `GET /teams/{id}/statistics` sin `tournamentId`, el
servicio falla porque no puede resolver el torneo activo.

### Reconocimiento — stub sin llamada HTTP real

Torneos tiene un hook `RecognitionAwardPort.triggerAwards()` invocado desde
`FinalizeTournamentService`, pero su única implementación es un `LogRecognitionAwardAdapter`
que solo registra un log. La llamada HTTP real a `POST /tournaments/{id}/recognitions`
de Estadísticas nunca se ejecuta.

**Riesgo**: el reconocimiento del torneo (máximo goleador, mejor defensa) nunca se
dispara automáticamente. Alguien debe llamar manualmente al endpoint.

### Puerto incorrecto

El servicio de Torneos corre en puerto **8080**, no 8081 como se había supuesto. Sus rutas
tampoco llevan el prefijo `/api/v1`.

---

## Arquitectura y decisiones

### PostgreSQL → MongoDB

El servicio se migró de PostgreSQL/JPA a MongoDB para alinearse con el ecosistema TechCup.
Las agregaciones SQL (`AVG`, `SUM`, `GROUP BY`) se reemplazaron por streams de Java sobre
documentos crudos. Es una compensación válida para el volumen de datos de un torneo
universitario.

### IDs como String

Los IDs (`playerId`, `teamId`, `matchId`, `tournamentId`) son `String` porque los demás
microservicios del ecosistema usan `ObjectId` de MongoDB. Esto fuerza conversiones en los
boundaries.

### Eventos RabbitMQ

El servicio consume eventos de Competencia vía RabbitMQ cuando un partido finaliza.
La integración está implementada pero **no validada extremo a extremo** — falta que
Competencia envíe eventos reales para confirmar que el formato del mensaje y la
deserialización funcionan.

---

<!--

### Template para próximos hallazgos

| Hallazgo | Impacto | Prioridad | Estado |
|---|---|---|---|
| Título corto | Bajo/Medio/Alto | Baja/Media/Alta/Crítica | Abierto/En progreso/Resuelto |

-->
