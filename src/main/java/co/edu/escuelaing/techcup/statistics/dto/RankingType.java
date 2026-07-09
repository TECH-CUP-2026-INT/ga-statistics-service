package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Tipos de ranking público que ofrece el servicio.
 * GOALS  -> botín de oro (más goles)
 * WINS   -> jugadores con más partidos ganados
 * FOULS  -> tabla de juego limpio (MENOS faltas es mejor)
 * MINUTES-> jugadores con más minutos acumulados
 */
public enum RankingType {
    GOALS,
    WINS,
    FOULS,
    MINUTES
}
