# Introducción

## Contexto

TechCup Fútbol es la plataforma que digitaliza el torneo semestral de fútbol de los
programas de Ingeniería de Sistemas, Inteligencia Artificial, Ciberseguridad e
Ingeniería Estadística de la Escuela Colombiana de Ingeniería Julio Garavito. Está
construida como un conjunto de microservicios, cada uno responsable de un dominio del
negocio (identidad, usuarios, equipos, torneos, competencia, logística, comunicaciones,
estadísticas, etc.).

## Este microservicio

`ga-statistics-service` es el dueño del dominio de **estadísticas**. No calcula nada por
sí mismo durante un partido — recibe los eventos ya resueltos desde el servicio de
Competencia (arbitraje en vivo) una vez el partido finaliza, y a partir de ahí calcula
todo lo demás: promedios, totales, rankings y reconocimientos.

## Problema que resuelve

Antes de este servicio, la información estadística de un torneo (goleadores, tabla de
posiciones, juego limpio) no existía de forma centralizada ni en tiempo real — dependía
de cálculos manuales o de revisar actas de partido dispersas. Este servicio:

- Centraliza toda la estadística en un solo lugar, consultable por cualquier otro
  servicio o por el frontend.
- Calcula todo **a partir de datos crudos** (un registro por jugador por partido), sin
  depender de que otro servicio le mande promedios ya calculados.
- Es de **solo lectura** para el resto del sistema salvo dos excepciones: el evento de
  ingesta (`POST /events`, que solo llama Competencia) y el disparo del reconocimiento
  (`POST /tournaments/{id}/recognitions`, que solo llama Torneos).

## Alcance

Cubre estadísticas de:

- **Jugador**: promedios y totales de goles, faltas, minutos, asistencias, tarjetas,
  partidos jugados, tasa de victorias.
- **Equipo**: récord de partidos (G/E/P), goles a favor/en contra, promedio de goles y
  faltas por partido, tabla de posiciones del torneo.
- **Torneo**: promedios generales por partido, tarjetas totales, rankings públicos
  (goleadores, juego limpio, valla menos vencida), reconocimiento oficial al finalizar.
- **Partido**: resultado por equipo (incluye walkover), tarjetas totales del encuentro.

No cubre: la programación de partidos, las alineaciones, ni la lógica de arbitraje en
vivo — eso pertenece al servicio de Competencia.
