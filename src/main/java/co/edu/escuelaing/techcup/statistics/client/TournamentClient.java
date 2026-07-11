package co.edu.escuelaing.techcup.statistics.client;

public interface TournamentClient {

    /**
     * Pregunta al servicio de Torneos cuál es el torneo activo actualmente.
     *
     * @return el id del torneo activo
     * @throws co.edu.escuelaing.techcup.statistics.exception.ExternalServiceException
     *         si el servicio de Torneos no responde, responde con error, o no
     *         hay ningún torneo activo en este momento.
     */
    Long getActiveTournamentId();
}
