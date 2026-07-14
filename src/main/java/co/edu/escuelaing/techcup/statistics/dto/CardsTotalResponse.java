package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Total de tarjetas en un alcance dado. "scope" indica si id es un matchId
 * o un tournamentId, para que la respuesta sea autoexplicativa.
 */
public record CardsTotalResponse(
        String scope,
        Long id,
        long yellowCards,
        long redCards
) {
}
