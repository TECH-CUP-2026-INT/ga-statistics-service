# Integración RabbitMQ — Estadísticas TechCup

## Broker compartido

Todos los servicios deben conectarse al **mismo broker RabbitMQ**.
Configuración común:

| Variable | Default |
|----------|---------|
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_PORT` | `5672` |
| `RABBITMQ_USER` | `guest` |
| `RABBITMQ_PASS` | `guest` |

## Exchange compartido

**Nombre:** `techcup.exchange`
**Tipo:** `Topic`

Todas las colas que quieran recibir eventos del sistema deben bindearse
a este exchange. Los servicios que publican eventos también deben
publicar aquí.

## Colas y routing keys

### 1. Eventos de partido → `techcup.statistics.match-events`

**Routing key:** `techcup.match.event.*`

**Payload esperado (`MatchEventMessage`):**

```json
{
  "eventId": "uuid",
  "playerId": "string",
  "teamId": "string",
  "matchId": "string",
  "tournamentId": "string",
  "result": "WON|DRAWN|LOST",
  "goals": 0,
  "yellowCards": 0,
  "redCards": 0,
  "foulsCommitted": 0,
  "minutesPlayed": 0,
  "assists": 0,
  "goalkeeper": false,
  "occurredAt": "2026-07-15T10:00:00"
}
```

**¿Quién publica?** → Servicio de **Competencia/Partidos** (astromerge)

**Ejemplo en Competencia (Spring Boot):**
```java
// 1. Agregar al pom.xml
// <dependency>
//   <groupId>org.springframework.boot</groupId>
//   <artifactId>spring-boot-starter-amqp</artifactId>
// </dependency>

// 2. Publicar evento
@Service
public class MatchEventPublisher {
    @Autowired private RabbitTemplate rabbitTemplate;

    public void publishMatchEvent(MatchEventMessage event) {
        rabbitTemplate.convertAndSend(
            "techcup.exchange",
            "techcup.match.event.finished",
            event
        );
    }
}
```

---

### 2. Eventos de torneo → `techcup.statistics.tournament-events`

**Routing key:** `techcup.tournament.event.*`

**Payload esperado (`TournamentEventMessage`):**

```json
{
  "eventType": "FINALIZED",
  "tournamentId": "uuid",
  "timestamp": "2026-07-15T10:00:00"
}
```

**Tipos de evento:**
- `FINALIZED` → El torneo finalizó. Estadísticas genera reconocimientos automáticamente.

**¿Quién publica?** → Servicio de **Torneos** (mortalkodebat)

**Ejemplo en Torneos (Spring Boot):**
```java
// Publicar cuando un torneo finaliza
rabbitTemplate.convertAndSend(
    "techcup.exchange",
    "techcup.tournament.event.finalized",
    new TournamentEventMessage("FINALIZED", tournamentId, LocalDateTime.now().toString())
);
```

> **Alternativa:** Torneos ya tiene configurado en local `StatisticsServiceFeignClient`
> que llama a `POST /tournaments/{id}/recognitions`. Pueden usar Feign o RabbitMQ,
> ambas funcionan. RabbitMQ es la opción async recomendada por el profe.

---

## Resumen de cambios necesarios por servicio

### 📌 Para el equipo de Competencia (astromerge)

1. Agregar `spring-boot-starter-amqp` al `pom.xml`
2. Configurar RabbitMQ en `application.yml` apuntando al mismo broker
3. Cuando finalice un partido, publicar el `MatchEventMessage` en `techcup.exchange`
   con routing key `techcup.match.event.finished`
4. Los campos `playerId`, `teamId`, `matchId`, `tournamentId` y `result` son obligatorios

### 📌 Para el equipo de Torneos (mortalkodebat) — via RabbitMQ (opcional)

1. Agregar `spring-boot-starter-amqp` al `pom.xml`
2. Configurar RabbitMQ en `application.properties`
3. Al finalizar un torneo, publicar `TournamentEventMessage` con `eventType=FINALIZED`
   en `techcup.exchange` con routing key `techcup.tournament.event.finalized`

> **Ya tienen Feign Client local.** Si prefieren no usar RabbitMQ, el Feign
> `StatisticsServiceFeignClient` funciona igual. Solo aseguren que
> `statistics-service.base-url=http://localhost:8085` esté configurado.

### 📌 Para el equipo de Notificaciones

Definir si comparten el exchange `techcup.exchange` o si prefieren uno propio.
Si usan el mismo exchange, pueden bindear sus colas a él sin problema.

### 📌 En Estadísticas (ghostapi) — ya implementado ✅

| Componente | Estado |
|---|---|
| `RabbitMQConfig` con exchange compartido | ✅ |
| `MatchEventConsumer` escuchando `techcup.statistics.match-events` | ✅ |
| `TournamentEventConsumer` escuchando `techcup.statistics.tournament-events` | ✅ |
| `MatchEventMessage` y `TournamentEventMessage` DTOs | ✅ |
| Config en `application.yml` | ✅ |
