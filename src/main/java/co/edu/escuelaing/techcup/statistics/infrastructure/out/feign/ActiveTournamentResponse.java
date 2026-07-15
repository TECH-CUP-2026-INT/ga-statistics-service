package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

/**
 * DTO de respuesta del servicio de Torneos al consultar el torneo activo.
 * <p>
 * Pendiente de confirmación: al revisar el código real de mk-tournament-service
 * no existe el endpoint GET /tournaments/active. Se necesita coordinar con
 * el equipo de Torneos para que lo agreguen.
 */
public record ActiveTournamentResponse(String id) {
}
