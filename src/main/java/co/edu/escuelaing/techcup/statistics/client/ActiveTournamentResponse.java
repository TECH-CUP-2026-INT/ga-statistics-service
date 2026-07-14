package co.edu.escuelaing.techcup.statistics.client;

/**
 * Forma esperada de la respuesta del servicio de Torneos al preguntar por el
 * torneo activo.
 *
 * PENDIENTE DE CONFIRMAR: al revisar el código real de mk-tournament-service
 * (2026-07-14), NO existe ningún endpoint GET /tournaments/active. El
 * servicio expone /tournaments/{id}/finalize, /tournaments/{id}/matchups,
 * /tournaments/history, etc., pero ninguno que devuelva "el torneo activo
 * actual". Hay que pedirle al equipo de Torneos que agreguen ese endpoint,
 * o cambiar el diseño de este lado para que quien llame a
 * /teams/{teamId}/statistics pase el tournamentId explícitamente en vez de
 * que este servicio intente resolver "el activo" por su cuenta.
 */
public record ActiveTournamentResponse(String id) {
}
