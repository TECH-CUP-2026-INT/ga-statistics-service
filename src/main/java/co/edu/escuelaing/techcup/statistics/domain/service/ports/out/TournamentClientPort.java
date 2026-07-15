package co.edu.escuelaing.techcup.statistics.domain.service.ports.out;

import co.edu.escuelaing.techcup.statistics.domain.exception.ExternalServiceException;

public interface TournamentClientPort {

    /**
     * @throws ExternalServiceException si el servicio de Torneos no responde
     *         o no reporta un torneo activo valido.
     */
    String getActiveTournamentId();
}
