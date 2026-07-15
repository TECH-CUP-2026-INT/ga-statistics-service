package co.edu.escuelaing.techcup.statistics.infrastructure.out.client;

/**
 * Forma esperada de la respuesta del servicio de Torneos al preguntar por el
 * torneo activo.
 *
 * PENDIENTE DE CONFIRMAR: al revisar el codigo real de mk-tournament-service
 * (2026-07-14), NO existe ningun endpoint GET /tournaments/active. Hay que
 * pedirle al equipo de Torneos que lo agreguen, o cambiar el diseno de este
 * lado.
 */
public record ActiveTournamentResponse(String id) {
}
