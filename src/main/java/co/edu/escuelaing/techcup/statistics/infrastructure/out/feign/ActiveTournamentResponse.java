package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import java.util.UUID;

/**
 * DTO de respuesta del servicio de Torneos al consultar el torneo activo.
 * <p>
 * El servicio de Torneos devuelve IDs como String (ObjectId de MongoDB).
 * Este record mantiene String para la deserialización y provee {@link #toTournamentId()}
 * para convertir al UUID que usa el dominio de Estadísticas.
 */
public record ActiveTournamentResponse(String id) {

    public UUID toTournamentId() {
        return id != null ? UUID.fromString(id) : null;
    }
}
