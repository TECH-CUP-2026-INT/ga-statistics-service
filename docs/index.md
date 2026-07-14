---
hide:
  - navigation
---

# Statistics Service — TechCup

<div class="grid cards" markdown>

-   **Servicio de Estadísticas**

    Microservicio responsable de calcular y exponer las estadísticas de jugadores, equipos, partidos y torneos: promedios, totales, rankings públicos y reconocimientos del torneo.

-   **Spring Boot 3.5.6 + MongoDB**

    Construido con Java 21 y persistencia en MongoDB. Los IDs (jugador, equipo, partido, torneo) son `String` para ser compatibles con los identificadores MongoDB del resto del ecosistema TechCup.

-   **97% de cobertura de tests**

    66 tests (servicio, controlador y cliente HTTP) con JaCoCo — ver [Pruebas](pruebas.md).

-   **Parte del ecosistema TechCup**

    Un microservicio dentro de la plataforma DOSW que digitaliza el torneo semestral de fútbol de la Escuela Colombiana de Ingeniería Julio Garavito.

</div>

---

## ¿Qué hace este servicio?

El `ga-statistics-service` centraliza toda la estadística del torneo, a partir de los eventos
que le envía el servicio de Competencia al finalizar cada partido:

- Calcula **promedios y totales por jugador**: goles, faltas, minutos jugados, asistencias, tarjetas.
- Calcula **estadísticas por equipo**: récord de partidos, goles a favor/en contra, tabla de posiciones.
- Genera **rankings públicos**: goleadores, juego limpio, valla menos vencida.
- Calcula y **guarda el reconocimiento del torneo** (máximo goleador, mejor defensa) al finalizar, manejando empates.
- Expone estadísticas **por partido**: resultado, tarjetas y promedios.

---

## Repositorio

```
https://github.com/TECH-CUP-2026-INT/ga-statistics-service
```

## Cómo empezar rápido

```bash
# 1. Clonar
git clone https://github.com/TECH-CUP-2026-INT/ga-statistics-service.git
cd ga-statistics-service

# 2. Levantar con Docker Compose (MongoDB incluido)
docker compose up --build

# 3. El servicio queda disponible en
http://localhost:8085

# 4. Documentación interactiva (Swagger)
http://localhost:8085/swagger-ui/index.html
```

[Ver configuración completa](configuracion.md){ .md-button .md-button--primary }
[Ver API REST](api.md){ .md-button }
