package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.TournamentClientPort;

/**
 * Interfaz del cliente HTTP para el servicio de Torneos.
 * La implementación concreta usa RestClient de Spring.
 */
public interface TournamentClient extends TournamentClientPort {
    // Hereda getActiveTournamentId() de TournamentClientPort
}
