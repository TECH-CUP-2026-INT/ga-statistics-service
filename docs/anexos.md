# Anexos

## Stack tecnológico

- Java 21
- Spring Boot 3.5.6 (Web, Data MongoDB, Validation)
- MongoDB
- Lombok
- springdoc-openapi (Swagger UI)
- JaCoCo (cobertura de tests)
- JUnit 5 + Mockito + AssertJ

## Enlaces útiles

- [Repositorio en GitHub](https://github.com/TECH-CUP-2026-INT/ga-statistics-service)
- [Swagger UI (con la app corriendo)](http://localhost:8085/swagger-ui/index.html)
- [Organización TECH-CUP-2026-INT](https://github.com/TECH-CUP-2026-INT)

## Glosario

| Término | Significado |
|---|---|
| Walkover | Partido en el que un equipo no se presenta; se registra como victoria (`WON`) para el presente y derrota (`LOST`) para el ausente |
| Valla menos vencida | Reconocimiento al portero (o equipo) con menos goles recibidos |
| Torneo activo | El torneo en curso actualmente, resuelto por el servicio de Torneos |

## Historial de decisiones relevantes

- **PostgreSQL → MongoDB**: el servicio se migró de PostgreSQL/JPA a MongoDB para
  alinearse con el resto del ecosistema TechCup, que usa MongoDB. Las agregaciones que
  antes se resolvían con SQL (`AVG`, `SUM`, `GROUP BY`) ahora se calculan en Java sobre
  los documentos crudos.
- **Long → String en los IDs**: los IDs se cambiaron de `Long` a `String` tras verificar
  que los demás microservicios usan IDs estilo MongoDB `ObjectId`, no numéricos.
