# Configuración

## Requisitos previos

- Java 21
- Maven (o el wrapper `mvnw` incluido)
- MongoDB (local, Docker, o Atlas)
- Docker y Docker Compose (opcional, para levantar todo junto)

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/techcup_statistics` | URI de conexión a MongoDB |
| `SERVER_PORT` | `8085` | Puerto en el que corre la app |
| `TOURNAMENTS_SERVICE_URL` | `http://localhost:8080` | Base URL del servicio de Torneos |

## Levantarlo localmente (sin Docker)

```bash
# Windows PowerShell
$env:MONGODB_URI="mongodb://localhost:27017/techcup_statistics"
mvn spring-boot:run
```

```bash
# Linux / Mac
export MONGODB_URI="mongodb://localhost:27017/techcup_statistics"
./mvnw spring-boot:run
```

## Levantarlo con Docker Compose

Incluye su propia instancia de MongoDB:

```bash
docker compose up --build
```

Esto levanta:

- `statistics-service` en el puerto `8085`
- `mongo` en el puerto `27017`

## Correr los tests

```bash
mvn test
```

Genera además el reporte de cobertura JaCoCo en `target/site/jacoco/index.html`.

## Documentación interactiva

Una vez levantado, la documentación Swagger/OpenAPI de la API queda disponible en:

```
http://localhost:8085/swagger-ui/index.html
```

## Este sitio de documentación (MkDocs)

Para verlo localmente:

```bash
pip install mkdocs-material --break-system-packages
mkdocs serve
```

Y abrir `http://localhost:8000`.
