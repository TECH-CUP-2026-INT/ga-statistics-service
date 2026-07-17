# Appendices

## Tech Stack

- Java 21
- Spring Boot 3.5.6 (Web, Data MongoDB, Validation)
- MongoDB
- Lombok
- springdoc-openapi (Swagger UI)
- JaCoCo (test coverage)
- JUnit 5 + Mockito + AssertJ

## Useful links

- [GitHub Repository](https://github.com/TECH-CUP-2026-INT/ga-statistics-service)
- [Swagger UI (with the app running)](http://localhost:8085/swagger-ui/index.html)
- [TECH-CUP-2026-INT Organization](https://github.com/TECH-CUP-2026-INT)

## Glossary

| Term | Meaning |
|---|---|
| Walkover | A match where one team does not show up; recorded as a win (`WON`) for the present team and a loss (`LOST`) for the absent team |
| Best defense | Recognition to the goalkeeper (or team) with the fewest goals conceded |
| Active tournament | The current ongoing tournament, resolved by the Tournament service |

## Decision log

- **PostgreSQL → MongoDB**: the service was migrated from PostgreSQL/JPA to MongoDB to align with the TechCup ecosystem. Aggregations previously done in SQL (`AVG`, `SUM`, `GROUP BY`) are now computed in Java over raw documents.
- **Long → String for IDs**: IDs were changed from `Long` to `String` after verifying that other microservices use MongoDB-style `ObjectId` IDs, not numeric ones.
