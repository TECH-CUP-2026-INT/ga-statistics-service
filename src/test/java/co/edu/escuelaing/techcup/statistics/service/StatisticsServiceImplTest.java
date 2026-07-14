package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.client.TournamentClient;
import co.edu.escuelaing.techcup.statistics.repository.TournamentRecognitionRepository;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.entity.MatchResult;
import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;
import co.edu.escuelaing.techcup.statistics.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private PlayerMatchStatRepository repository;

    @Mock
    private TournamentClient tournamentClient;

    @Mock
    private TournamentRecognitionRepository recognitionRepository;

    private StatisticsService statisticsService;

    private static final Long PLAYER_ID = 1L;
    private static final Long TEAM_ID = 10L;
    private static final Long TOURNAMENT_ID = 100L;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsServiceImpl(repository, recognitionRepository, tournamentClient);
    }

    private PlayerMatchStat stat(Long playerId, Long teamId, Long matchId, MatchResult result,
                                  int goals, int fouls, int minutes) {
        return PlayerMatchStat.builder()
                .playerId(playerId)
                .teamId(teamId)
                .matchId(matchId)
                .tournamentId(TOURNAMENT_ID)
                .result(result)
                .goals(goals)
                .foulsCommitted(fouls)
                .minutesPlayed(minutes)
                .build();
    }

    // ---------- registerMatchStat ----------

    @Test
    void registerMatchStat_deberiaGuardarCuandoNoEsDuplicado() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, 500L, TOURNAMENT_ID, MatchResult.WON,
                2, 1, 0, 3, 90, 1, false);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 500L)).thenReturn(false);

        statisticsService.registerMatchStat(request);

        ArgumentCaptor<PlayerMatchStat> captor = ArgumentCaptor.forClass(PlayerMatchStat.class);
        verify(repository).save(captor.capture());

        PlayerMatchStat saved = captor.getValue();
        assertThat(saved.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(saved.getMatchId()).isEqualTo(500L);
        assertThat(saved.getGoals()).isEqualTo(2);
        assertThat(saved.getResult()).isEqualTo(MatchResult.WON);
    }

    @Test
    void registerMatchStat_deberiaRellenarConCeroLosCamposNulos() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, 501L, TOURNAMENT_ID, MatchResult.LOST,
                null, null, null, null, null, null, null);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 501L)).thenReturn(false);

        statisticsService.registerMatchStat(request);

        ArgumentCaptor<PlayerMatchStat> captor = ArgumentCaptor.forClass(PlayerMatchStat.class);
        verify(repository).save(captor.capture());

        PlayerMatchStat saved = captor.getValue();
        assertThat(saved.getGoals()).isZero();
        assertThat(saved.getMinutesPlayed()).isZero();
    }

    @Test
    void registerMatchStat_deberiaLanzarExcepcionSiYaExisteElPartidoParaElJugador() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, 500L, TOURNAMENT_ID, MatchResult.WON,
                2, 0, 0, 1, 90, 0, false);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 500L)).thenReturn(true);

        assertThrows(DuplicateMatchStatException.class,
                () -> statisticsService.registerMatchStat(request));

        verify(repository, never()).save(any());
    }

    // ---------- Promedios de jugador ----------

    @Test
    void getAverageGoals_deberiaCalcularElPromedioYRedondear() {
        List<PlayerMatchStat> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.LOST, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 3L, MatchResult.WON, 2, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResponse response = statisticsService.getAverageGoals(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(1.67);
        assertThat(response.matchesConsidered()).isEqualTo(3L);
    }

    @Test
    void getAverageWinRate_deberiaCalcularPorcentajeCorrectamente() {
        List<PlayerMatchStat> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 3L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 4L, MatchResult.LOST, 0, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResponse response = statisticsService.getAverageWinRate(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(75.0);
    }

    @Test
    void getAverageWinRate_deberiaDevolverCeroSiNoHaJugadoPartidos() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of());

        PlayerAverageResponse response = statisticsService.getAverageWinRate(PLAYER_ID, null);

        assertThat(response.value()).isZero();
        assertThat(response.matchesConsidered()).isZero();
    }

    @Test
    void getMatchesPlayed_deberiaContarLosPartidosDelJugador() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.LOST, 0, 0, 90)));

        MatchesPlayedResponse response = statisticsService.getMatchesPlayed(PLAYER_ID, null);

        assertThat(response.matchesPlayed()).isEqualTo(2L);
    }

    // ---------- Rankings ----------

    @Test
    void getRanking_goles_deberiaOrdenarDescendente() {
        List<PlayerMatchStat> stats = List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 5, 0, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.LOST, 2, 0, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        RankingResponse response = statisticsService.getRanking(RankingType.GOALS, TOURNAMENT_ID, 10);

        assertThat(response.entries()).hasSize(2);
        assertThat(response.entries().get(0).playerId()).isEqualTo(1L);
        assertThat(response.entries().get(0).value()).isEqualTo(5L);
        assertThat(response.entries().get(0).position()).isEqualTo(1);
    }

    @Test
    void getRanking_faltas_deberiaOrdenarAscendente() {
        List<PlayerMatchStat> stats = List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 0, 5, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.LOST, 0, 1, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        RankingResponse response = statisticsService.getRanking(RankingType.FOULS, TOURNAMENT_ID, 10);

        // El jugador con MENOS faltas va primero (tabla de juego limpio).
        assertThat(response.entries().get(0).playerId()).isEqualTo(2L);
        assertThat(response.entries().get(0).value()).isEqualTo(1L);
    }

    // ---------- Estadísticas de equipo ----------

    @Test
    void getTeamStatisticsInActiveTournament_deberiaResolverElTorneoActivo() {
        when(tournamentClient.getActiveTournamentId()).thenReturn(TOURNAMENT_ID);
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90)));
        when(repository.findByMatchId(1L)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(9L, 99L, 1L, MatchResult.LOST, 1, 0, 90)));

        var response = statisticsService.getTeamStatisticsInActiveTournament(TEAM_ID);

        assertThat(response.matchesPlayed()).isEqualTo(1L);
        assertThat(response.wins()).isEqualTo(1L);
        assertThat(response.goalsFor()).isEqualTo(4L);
        assertThat(response.goalsAgainst()).isEqualTo(1L);
        assertThat(response.points()).isEqualTo(3L);
    }
}
