package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Una fila del ranking: la posición y el jugador con su valor acumulado.
 * Nota: este servicio solo conoce el playerId; el nombre/foto del jugador
 * lo enriquece el frontend o el orquestador consultando el servicio de
 * usuarios y jugadores (cada microservicio es dueño solo de su dominio).
 */
public record RankingEntryResponse(
        int position,
        Long playerId,
        long value
) {
}
