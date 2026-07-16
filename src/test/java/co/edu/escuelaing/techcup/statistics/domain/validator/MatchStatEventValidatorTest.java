package co.edu.escuelaing.techcup.statistics.domain.validator;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchStatEventValidatorTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID MATCH_ID = UUID.randomUUID();
    private static final UUID TOURNAMENT_ID = UUID.randomUUID();

    private PlayerMatchStatistic validStat() {
        return PlayerMatchStatistic.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId(MATCH_ID).tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).goals(2).yellowCards(1).redCards(0)
                .foulsCommitted(3).minutesPlayed(90).assists(1).goalkeeper(false)
                .build();
    }

    @Test
    void validate_deberiaPasarCuandoTodoEsValido() {
        assertDoesNotThrow(() -> MatchStatEventValidator.validate(validStat()));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoPlayerIdEsNull() {
        var stat = validStat(); stat.setPlayerId(null);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoTeamIdEsNull() {
        var stat = validStat(); stat.setTeamId(null);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoMatchIdEsNull() {
        var stat = validStat(); stat.setMatchId(null);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaPasarCuandoTournamentIdEsNull() {
        var stat = validStat(); stat.setTournamentId(null);
        assertDoesNotThrow(() -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoResultEsNull() {
        var stat = validStat(); stat.setResult(null);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoGoalsEsNegativo() {
        var stat = validStat(); stat.setGoals(-1);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoYellowCardsEsNegativo() {
        var stat = validStat(); stat.setYellowCards(-1);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaLanzarExcepcionCuandoFoulsEsNegativo() {
        var stat = validStat(); stat.setFoulsCommitted(-1);
        assertThrows(IllegalArgumentException.class, () -> MatchStatEventValidator.validate(stat));
    }

    @Test
    void validate_deberiaPasarConCamposNulosOpcionales() {
        var stat = PlayerMatchStatistic.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId(MATCH_ID).tournamentId(TOURNAMENT_ID)
                .result(MatchResult.DRAWN).build();
        assertDoesNotThrow(() -> MatchStatEventValidator.validate(stat));
    }
}
