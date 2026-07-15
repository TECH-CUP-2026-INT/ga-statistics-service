# Integración RabbitMQ — Estadísticas TechCup

## Broker compartido

Todos los servicios apuntan al **mismo** CloudAMQP:

| Variable | Valor |
|----------|-------|
| Host | `toucan.lmq.cloudamqp.com` |
| Puerto | `5671` (TLS) |
| Usuario | `exdntvvm` |
| Password | Preguntar a Juan David |
| Virtual Host | `exdntvvm` |

## Exchange compartido

**Nombre:** `techcup.exchange`
**Tipo:** `Topic`

Todas las colas se bindean aquí.

---

## 📌 Para astromerge (Competencia)

**1. Dependencia en pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

**2. Config en application.yml:**
```yaml
spring:
  rabbitmq:
    host: toucan.lmq.cloudamqp.com
    port: 5671
    username: exdntvvm
    password: ${RABBITMQ_PASS}
    virtual-host: exdntvvm
    ssl:
      enabled: true
```

**3. Publicar evento cuando termina un partido (por cada jugador):**
```java
@Service
public class MatchEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishPlayerStat(String playerId, String teamId, String matchId,
                                   String tournamentId, MatchResult result,
                                   Integer goals, Integer yellowCards, Integer redCards,
                                   Integer fouls, Integer minutes, Integer assists,
                                   Boolean goalkeeper) {
        var event = new MatchStatEvent(
            playerId, teamId, matchId, tournamentId, result,
            goals, yellowCards, redCards, fouls, minutes, assists,
            goalkeeper, LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend("techcup.exchange", "techcup.match.event.stat", event);
    }

    record MatchStatEvent(
        String playerId, String teamId, String matchId, String tournamentId,
        MatchResult result, Integer goals, Integer yellowCards, Integer redCards,
        Integer foulsCommitted, Integer minutesPlayed, Integer assists,
        Boolean goalkeeper, LocalDateTime occurredAt
    ) {}
}
```

**Payload que envía:**
```json
{
  "playerId": "p1",
  "teamId": "t1",
  "matchId": "m1",
  "tournamentId": "tn1",
  "result": "WON",
  "goals": 2,
  "yellowCards": 1,
  "redCards": 0,
  "foulsCommitted": 3,
  "minutesPlayed": 90,
  "assists": 1,
  "goalkeeper": false,
  "occurredAt": "2026-07-15T18:00:00"
}
```

---

## 📌 Para mortalkodebat (Torneos)

**1. Dependencia en pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

**2. Config en application.properties:**
```properties
spring.rabbitmq.host=toucan.lmq.cloudamqp.com
spring.rabbitmq.port=5671
spring.rabbitmq.username=exdntvvm
spring.rabbitmq.password=${RABBITMQ_PASS}
spring.rabbitmq.virtual-host=exdntvvm
spring.rabbitmq.ssl.enabled=true
```

**3. Publicar cuando finaliza un torneo:**
```java
// En FinalizeTournamentService, después de guardar el torneo como FINISHED:
rabbitTemplate.convertAndSend(
    "techcup.exchange",
    "techcup.tournament.event.finalized",
    new TournamentFinalizedEvent(tournamentId, LocalDateTime.now())
);

// DTO necesario:
record TournamentFinalizedEvent(String tournamentId, LocalDateTime occurredAt) {}
```

> **Alternativa:** Ya tienen `StatisticsServiceFeignClient` que llama a
> `POST /tournaments/{id}/recognitions`. Pueden usar Feign o RabbitMQ.

---

## 📌 Para ghostapi (Estadísticas) — ✅ ya implementado

| Componente | Estado |
|---|---|
| `RabbitMQConfig` con exchange `techcup.exchange` | ✅ |
| `MatchEventConsumer` escucha `techcup.statistics.match-events` | ✅ |
| `TournamentEventConsumer` escucha `techcup.statistics.tournament-events` | ✅ |
| `MatchStatEvent` y `TournamentFinalizedEvent` DTOs | ✅ |
| Config completa en `application.yml` | ✅ |
| Azure Container Apps funcionando | ✅ |
| RabbitMQ CloudAMQP conectado | ✅ |
