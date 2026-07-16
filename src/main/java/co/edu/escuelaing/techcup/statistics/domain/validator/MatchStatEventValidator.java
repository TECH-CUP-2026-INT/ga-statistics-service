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

        checkRequired(errors, statistic.getPlayerId(), "El identificador del jugador (playerId) es obligatorio");
        checkRequired(errors, statistic.getTeamId(), "El identificador del equipo (teamId) es obligatorio");
        checkRequired(errors, statistic.getMatchId(), "El identificador del partido (matchId) es obligatorio");
        // tournamentId es opcional: Competencia no lo envia hoy
        checkRequired(errors, statistic.getResult(), "El resultado del partido (result) es obligatorio");
        checkNotNegative(errors, statistic.getGoals(), "goles");
        checkNotNegative(errors, statistic.getYellowCards(), "tarjetas amarillas");
        checkNotNegative(errors, statistic.getRedCards(), "tarjetas rojas");
        checkNotNegative(errors, statistic.getFoulsCommitted(), "faltas cometidas");
        checkNotNegative(errors, statistic.getMinutesPlayed(), "minutos jugados");
        checkNotNegative(errors, statistic.getAssists(), "asistencias");

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Errores de validación en los datos de entrada: " + String.join("; ", errors));
        }
    }

    private static void checkRequired(List<String> errors, Object value, String message) {
        if (value == null) {
            errors.add(message);
        }
    }

    private static void checkNotNegative(List<String> errors, Integer value, String fieldName) {
        if (value != null && value < 0) {
            errors.add("El número de " + fieldName + " no puede ser negativo");
        }
    }
}
