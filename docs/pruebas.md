# Pruebas

## Resumen

66 tests, 97% de cobertura de instrucciones (JaCoCo), 80% de cobertura de ramas.

| Paquete | Cobertura |
|---|---|
| `dto` | 100% |
| `controller` | 100% |
| `client` | 100% |
| `entity` | 100% |
| `service` | 97% |
| `exception` | 85% |

## Cómo se organizan

### `StatisticsServiceImplTest` (35 tests)

Tests unitarios de la lógica de negocio con Mockito, simulando `PlayerMatchStatRepository`,
`TournamentRecognitionRepository` y `TournamentClient`. Cubre: registro de eventos
(incluyendo duplicados y valores nulos), todos los promedios y totales de jugador,
estadísticas de equipo, rankings (con y sin filtro de torneo), reconocimiento del
torneo (incluyendo manejo de empates), y todos los endpoints de partido/torneo.

### `StatisticsControllerTest` (29 tests)

Tests del controlador con `@WebMvcTest` y `MockMvc`, simulando `StatisticsService`. No
necesita MongoDB ni el servicio de Torneos corriendo. Cubre los 25 endpoints, más los
casos de error (`400` de validación, `409` de duplicado, `404` de reconocimiento no
generado, `502` cuando Torneos no responde).

### `TournamentClientImplTest` (4 tests)

Tests del cliente HTTP hacia Torneos, simulando `RestClient` con Mockito
(`Answers.RETURNS_DEEP_STUBS`) — no hace llamadas de red reales. Cubre respuesta válida,
respuesta nula, id nulo, y falla de conexión.

### `ServiceStatisticsApplicationTests` (1 test)

Test de integración que levanta el contexto completo de Spring, incluyendo una conexión
real a MongoDB (`contextLoads`).

## Correr los tests

```bash
mvn test
```

## Ver el reporte de cobertura

Después de correr los tests, abrir:

```
target/site/jacoco/index.html
```

## Nota sobre el test de contexto

`ServiceStatisticsApplicationTests` necesita MongoDB real corriendo (a diferencia de los
demás, que usan mocks). Si `MONGODB_URI` no está exportado, usa el valor por defecto
`mongodb://localhost:27017/techcup_statistics`.
