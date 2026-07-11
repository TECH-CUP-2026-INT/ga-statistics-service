package co.edu.escuelaing.techcup.statistics.client;

/**
 * Forma esperada de la respuesta del servicio de Torneos al preguntar por el
 * torneo activo.
 *
 * SUPUESTO PENDIENTE DE CONFIRMAR con el equipo de Torneos: se asume que
 * expone GET /api/v1/tournaments/active y devuelve un JSON que al menos
 * contiene el campo "id". Los demás campos que devuelva de más se ignoran.
 * Si el contrato real es distinto, solo hay que ajustar esta clase y la URL
 * en application.properties / TournamentClientImpl.
 */
public record ActiveTournamentResponse(Long id) {
}
