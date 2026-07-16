package co.edu.escuelaing.techcup.statistics.domain.validator;

import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de dominio para eventos de estadísticas de partido.
 * Separa las validaciones de negocio de la lógica de servicio.
 */
public class MatchStatEventValidator {

    private MatchStatEventValidator() {
        // Clase utilitaria, no se instancia
    }

    /**
     * Valida que un objeto {@link PlayerMatchStatistic} tenga los campos
     * requeridos para ser persistido.
     *
     * @param statistic la estadística a validar
     * @throws IllegalArgumentException si hay errores de validación
     */
    public static void validate(PlayerMatchStatistic statistic) {
        List<String> errors = new ArrayList<>();

        if (statistic.getPlayerId() == null) {
            errors.add("El identificador del jugador (playerId) es obligatorio");
        }
        if (statistic.getTeamId() == null) {
            errors.add("El identificador del equipo (teamId) es obligatorio");
        }
        if (statistic.getMatchId() == null) {
            errors.add("El identificador del partido (matchId) es obligatorio");
        }
        // tournamentId es opcional: Competencia no lo envia hoy
        if (statistic.getResult() == null) {
            errors.add("El resultado del partido (result) es obligatorio");
        }
        if (statistic.getGoals() != null && statistic.getGoals() < 0) {
            errors.add("El número de goles no puede ser negativo");
        }
        if (statistic.getYellowCards() != null && statistic.getYellowCards() < 0) {
            errors.add("El número de tarjetas amarillas no puede ser negativo");
        }
        if (statistic.getRedCards() != null && statistic.getRedCards() < 0) {
            errors.add("El número de tarjetas rojas no puede ser negativo");
        }
        if (statistic.getFoulsCommitted() != null && statistic.getFoulsCommitted() < 0) {
            errors.add("El número de faltas cometidas no puede ser negativo");
        }
        if (statistic.getMinutesPlayed() != null && statistic.getMinutesPlayed() < 0) {
            errors.add("Los minutos jugados no pueden ser negativos");
        }
        if (statistic.getAssists() != null && statistic.getAssists() < 0) {
            errors.add("El número de asistencias no puede ser negativo");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Errores de validación en los datos de entrada: " + String.join("; ", errors));
        }
    }
}
