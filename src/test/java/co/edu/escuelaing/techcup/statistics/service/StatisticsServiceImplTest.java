package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.entity.MatchResult;
import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;
import co.edu.escuelaing.techcup.statistics.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository.RankingRow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private PlayerMatchStatRepository repository;
    @Mock
    private co.edu.escuelaing.techcup.statistics.client.TournamentClient tournamentClient;

    private StatisticsService statisticsService;

    private static final Long PLAYER_ID = 1L;
    private static final Long TOURNAMENT_ID = 100L;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsServiceImpl(repository, tournamentClient);
    }
    // ---------- registerMatchStat ----------

    @Test
    void registerMatchStat_deberiaGuardarCuandoNoEsDuplicado() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, 10L, 500L, TOURNAMENT_ID, MatchResult.WON,
                2, 1, 0, 3, 90);

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
                PLAYER_ID, 10L, 501L, TOURNAMENT_ID, MatchResult.LOST,
                null, null, null, null, null);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 501L)).thenReturn(false);

        statisticsService.registerMatchStat(request);

        ArgumentCaptor<PlayerMatchStat> captor = ArgumentCaptor.forClass(PlayerMatchStat.class);
        verify(repository).save(captor.capture());

        PlayerMatchStat saved = captor.getValue();
        assertThat(saved.getGoals()).isZero();
        assertThat(saved.getYellowCards()).isZero();
        assertThat(saved.getMinutesPlayed()).isZero();
    }

    @Test
    void registerMatchStat_deberiaLanzarExcepcionSiYaExisteElPartidoParaElJugador() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, 10L, 500L, TOURNAMENT_ID, MatchResult.WON,
                2, 0, 0, 1, 90);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 500L)).thenReturn(true);

        assertThrows(DuplicateMatchStatException.class,
                () -> statisticsService.registerMatchStat(request));

        verify(repository, never()).save(any());
    }

    // ---------- Promedios ----------

    @Test
    void getAverageGoals_deberiaRedondearADosDecimales() {
        when(repository.countMatchesPlayed(PLAYER_ID, null)).thenReturn(3L);
        when(repository.averageGoals(PLAYER_ID, null)).thenReturn(1.6666666);

        PlayerAverageResponse response = statisticsService.getAverageGoals(PLAYER_ID, null);

        assertThat(response.value()).isEqualTo(1.67);
        assertThat(response.matchesConsidered()).isEqualTo(3L);
        assertThat(response.metric()).isEqualTo("averageGoals");
    }

    @Test
    void getAverageWinRate_deberiaCalcularPorcentajeCorrectamente() {
        when(repository.countMatchesPlayed(PLAYER_ID, TOURNAMENT_ID)).thenReturn(4L);
        when(repository.countMatchesWon(PLAYER_ID, TOURNAMENT_ID)).thenReturn(3L);

        PlayerAverageResponse response = statisticsService.getAverageWinRate(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(75.0);
        assertThat(response.tournamentId()).isEqualTo(TOURNAMENT_ID);
    }

    @Test
    void getAverageWinRate_deberiaDevolverCeroSiNoHaJugadoPartidos() {
        when(repository.countMatchesPlayed(PLAYER_ID, null)).thenReturn(0L);

        PlayerAverageResponse response = statisticsService.getAverageWinRate(PLAYER_ID, null);

        assertThat(response.value()).isZero();
        assertThat(response.matchesConsidered()).isZero();
        // Como no jugo partidos, nunca deberia preguntarse cuantos gano.
        verify(repository, never()).countMatchesWon(anyLong(), any());
    }

    @Test
    void getAverageFouls_deberiaUsarElRepositorio() {
        when(repository.countMatchesPlayed(PLAYER_ID, null)).thenReturn(2L);
        when(repository.averageFouls(PLAYER_ID, null)).thenReturn(2.5);

        PlayerAverageResponse response = statisticsService.getAverageFouls(PLAYER_ID, null);

        assertThat(response.value()).isEqualTo(2.5);
        assertThat(response.metric()).isEqualTo("averageFouls");
    }

    @Test
    void getAverageMinutesPlayed_deberiaUsarElRepositorio() {
        when(repository.countMatchesPlayed(PLAYER_ID, null)).thenReturn(2L);
        when(repository.averageMinutesPlayed(PLAYER_ID, null)).thenReturn(75.0);

        PlayerAverageResponse response = statisticsService.getAverageMinutesPlayed(PLAYER_ID, null);

        assertThat(response.value()).isEqualTo(75.0);
        assertThat(response.metric()).isEqualTo("averageMinutesPlayed");
    }

    // ---------- Partidos jugados ----------

    @Test
    void getMatchesPlayed_deberiaDevolverElConteoDelRepositorio() {
        when(repository.countMatchesPlayed(PLAYER_ID, TOURNAMENT_ID)).thenReturn(5L);

        MatchesPlayedResponse response = statisticsService.getMatchesPlayed(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.matchesPlayed()).isEqualTo(5L);
        assertThat(response.playerId()).isEqualTo(PLAYER_ID);
    }

    // ---------- Rankings ----------

    @Test
    void getRanking_deberiaAsignarPosicionesEnOrden() {
        RankingRow row1 = mockRankingRow(1L, 10L);
        RankingRow row2 = mockRankingRow(2L, 7L);

        when(repository.findGoalsRanking(eq(null), any(Pageable.class)))
                .thenReturn(List.of(row1, row2));

        RankingResponse response = statisticsService.getRanking(RankingType.GOALS, null, 10);

        assertThat(response.type()).isEqualTo("GOALS");
        assertThat(response.entries()).hasSize(2);
        assertThat(response.entries().get(0).position()).isEqualTo(1);
        assertThat(response.entries().get(0).playerId()).isEqualTo(1L);
        assertThat(response.entries().get(0).value()).isEqualTo(10L);
        assertThat(response.entries().get(1).position()).isEqualTo(2);
    }

    @Test
    void getRanking_deberiaUsarElRepositorioCorrectoSegunElTipo() {
        when(repository.findFairPlayRanking(eq(TOURNAMENT_ID), any(Pageable.class)))
                .thenReturn(List.of());

        statisticsService.getRanking(RankingType.FOULS, TOURNAMENT_ID, 5);

        verify(repository).findFairPlayRanking(eq(TOURNAMENT_ID), any(Pageable.class));
        verify(repository, never()).findGoalsRanking(any(), any());
        verify(repository, never()).findWinsRanking(any(), any());
        verify(repository, never()).findMinutesRanking(any(), any());
    }

    private RankingRow mockRankingRow(Long playerId, Long value) {
        RankingRow row = org.mockito.Mockito.mock(RankingRow.class);
        when(row.getPlayerId()).thenReturn(playerId);
        when(row.getValue()).thenReturn(value);
        return row;
    }
}