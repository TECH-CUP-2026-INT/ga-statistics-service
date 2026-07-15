package co.edu.escuelaing.techcup.statistics.domain.service.ports.out;

/**
 * Puerto de salida para la comunicación con el servicio de Torneos.
 * Define el contrato que el dominio necesita, sin depender de
 * tecnologías HTTP concretas.
 */
public interface TournamentClientPort {

    /**
     * Obtiene el identificador del torneo activo actual.
     *
     * @return ID del torneo activo
     * @throws co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException
     *         si el servicio no está disponible o no hay torneo activo
     */
    String getActiveTournamentId();
}
