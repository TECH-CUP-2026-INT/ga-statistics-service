package co.edu.escuelaing.techcup.statistics.domain.model;

/**
 * Tipos de ranking público que ofrece el servicio de estadísticas.
 * <ul>
 *   <li><b>GOALS</b> — Botín de oro (más goles)</li>
 *   <li><b>WINS</b> — Jugadores con más partidos ganados</li>
 *   <li><b>FOULS</b> — Tabla de juego limpio (menos faltas es mejor)</li>
 *   <li><b>MINUTES</b> — Jugadores con más minutos acumulados</li>
 * </ul>
 */
public enum RankingType {
    GOALS,
    WINS,
    FOULS,
    MINUTES
}
