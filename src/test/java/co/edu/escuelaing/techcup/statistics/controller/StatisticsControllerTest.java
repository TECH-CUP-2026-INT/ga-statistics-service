package co.edu.escuelaing.techcup.statistics.controller;

import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingEntryResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.domain.MatchResult;
import co.edu.escuelaing.techcup.statistics.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.exception.ExternalServiceException;
import co.edu.escuelaing.techcup.statistics.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.mapper.PlayerMatchStatMapperImpl;
import co.edu.escuelaing.techcup.statistics.service.StatisticsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@Import(PlayerMatchStatMapperImpl.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatisticsService statisticsService;

    private static final String PLAYER_ID = "player-1";
    private static final String TEAM_ID = "team-10";
    private static final String TOURNAMENT_ID = "tournament-100";
    private static final String MATCH_ID = "match-500";

    // ---------- Ingesta ----------

    @Test
    void registerMatchStat_deberiaDevolver201CuandoEsValido() throws Exception {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, MATCH_ID, TOURNAMENT_ID, MatchResult.WON,
                2, 1, 0, 3, 90, 1, false);

        mockMvc.perform(post("/api/v1/statistics/events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerMatchStat_deberiaDevolver400CuandoFaltaUnCampoObligatorio() throws Exception {
        String invalidJson = "{\"teamId\": \"team-10\", \"matchId\": \"match-500\"}";

        mockMvc.perform(post("/api/v1/statistics/events")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerMatchStat_deberiaDevolver409CuandoEsDuplicado() throws Exception {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, MATCH_ID, TOURNAMENT_ID, MatchResult.WON,
                2, 0, 0, 1, 90, 0, false);

        doThrow(new DuplicateMatchStatException(PLAYER_ID, MATCH_ID))
                .when(statisticsService).registerMatchStat(any());

        mockMvc.perform(post("/api/v1/statistics/events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ---------- Jugador ----------

    @Test
    void getAverageWinRate_deberiaDevolver200() throws Exception {
        when(statisticsService.getAverageWinRate(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerAverageResponse(PLAYER_ID, TOURNAMENT_ID, "averageWinRatePercentage", 50.0, 2));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-win-rate", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(50.0));
    }

    @Test
    void getAverageGoals_deberiaDevolver200() throws Exception {
        when(statisticsService.getAverageGoals(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerAverageResponse(PLAYER_ID, null, "averageGoals", 1.5, 2));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-goals", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metric").value("averageGoals"));
    }

    @Test
    void getAverageFouls_deberiaDevolver200() throws Exception {
        when(statisticsService.getAverageFouls(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerAverageResponse(PLAYER_ID, null, "averageFouls", 2.0, 2));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-fouls", PLAYER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getAverageMinutesPlayed_deberiaDevolver200() throws Exception {
        when(statisticsService.getAverageMinutesPlayed(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerAverageResponse(PLAYER_ID, null, "averageMinutesPlayed", 75.0, 2));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-minutes-played", PLAYER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getMatchesPlayed_deberiaDevolver200() throws Exception {
        when(statisticsService.getMatchesPlayed(eq(PLAYER_ID), any()))
                .thenReturn(new MatchesPlayedResponse(PLAYER_ID, null, 5));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/matches-played", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchesPlayed").value(5));
    }

    @Test
    void getPlayerTotalGoals_deberiaDevolver200() throws Exception {
        when(statisticsService.getPlayerTotalGoals(eq(PLAYER_ID), any()))
                .thenReturn(new TotalResponse(PLAYER_ID, null, "totalGoals", 5, 3));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/total-goals", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5));
    }

    @Test
    void getPlayerTotalFouls_deberiaDevolver200() throws Exception {
        when(statisticsService.getPlayerTotalFouls(eq(PLAYER_ID), any()))
                .thenReturn(new TotalResponse(PLAYER_ID, null, "totalFouls", 3, 3));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/total-fouls", PLAYER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getPlayerTotalAssists_deberiaDevolver200() throws Exception {
        when(statisticsService.getPlayerTotalAssists(eq(PLAYER_ID), any()))
                .thenReturn(new TotalResponse(PLAYER_ID, null, "totalAssists", 2, 3));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/assists", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void getPlayerCards_deberiaDevolver200() throws Exception {
        when(statisticsService.getPlayerCards(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerCardsResponse(PLAYER_ID, null, 2, 0));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/cards", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yellowCards").value(2));
    }

    // ---------- Rankings ----------

    @Test
    void getRanking_deberiaDevolver200() throws Exception {
        when(statisticsService.getRanking(eq(RankingType.GOALS), any(), eq(10)))
                .thenReturn(new RankingResponse("GOALS", null, List.of(new RankingEntryResponse(1, PLAYER_ID, 5))));

        mockMvc.perform(get("/api/v1/statistics/rankings").param("type", "GOALS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries[0].playerId").value(PLAYER_ID));
    }

    @Test
    void getGoalkeeperRanking_deberiaDevolver200() throws Exception {
        when(statisticsService.getGoalkeeperRanking(any(), eq(10)))
                .thenReturn(new GoalkeeperRankingResponse(TOURNAMENT_ID,
                        List.of(new GoalkeeperRankingResponse.Entry(1, PLAYER_ID, 0))));

        mockMvc.perform(get("/api/v1/statistics/goalkeeper-ranking").param("tournamentId", TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries[0].goalsConceded").value(0));
    }

    // ---------- Equipo ----------

    @Test
    void getTeamStatisticsInActiveTournament_deberiaDevolver200() throws Exception {
        when(statisticsService.getTeamStatisticsInActiveTournament(TEAM_ID))
                .thenReturn(new TeamStatisticsResponse(TEAM_ID, TOURNAMENT_ID, 2, 1, 0, 1, 3, 2, 1, 3));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(3));
    }

    @Test
    void getTeamStatisticsInActiveTournament_deberiaDevolver502SiTorneosNoResponde() throws Exception {
        when(statisticsService.getTeamStatisticsInActiveTournament(TEAM_ID))
                .thenThrow(new ExternalServiceException("Torneos no disponible"));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TEAM_ID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getTeamMatchRecord_deberiaDevolver200() throws Exception {
        when(statisticsService.getTeamMatchRecord(eq(TEAM_ID), any()))
                .thenReturn(new TeamMatchRecordResponse(TEAM_ID, null, 4, 2, 1, 1, 50.0, 25.0, 25.0));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/match-record", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winRatePercentage").value(50.0));
    }

    @Test
    void getTeamAverageGoals_deberiaDevolver200() throws Exception {
        when(statisticsService.getTeamAverageGoals(eq(TEAM_ID), any()))
                .thenReturn(new TeamAverageResponse(TEAM_ID, null, "averageGoalsPerMatch", 2.5, 2));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/average-goals", TEAM_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTeamAverageFouls_deberiaDevolver200() throws Exception {
        when(statisticsService.getTeamAverageFouls(eq(TEAM_ID), any()))
                .thenReturn(new TeamAverageResponse(TEAM_ID, null, "averageFoulsPerMatch", 4.0, 2));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/average-fouls", TEAM_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTeamTotalFouls_deberiaDevolver200() throws Exception {
        when(statisticsService.getTeamTotalFouls(eq(TEAM_ID), any()))
                .thenReturn(new TotalResponse(TEAM_ID, null, "totalFouls", 8, 2));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/total-fouls", TEAM_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTeamGoals_deberiaDevolver200() throws Exception {
        when(statisticsService.getTeamGoals(eq(TEAM_ID), any()))
                .thenReturn(new TeamGoalsResponse(TEAM_ID, null, 5, 2, 3));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/goals", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalDifference").value(3));
    }

    // ---------- Torneo ----------

    @Test
    void getTournamentStandings_deberiaDevolver200() throws Exception {
        when(statisticsService.getTournamentStandings(TOURNAMENT_ID))
                .thenReturn(new TournamentStandingsResponse(TOURNAMENT_ID, List.of(
                        new TeamStatisticsResponse(TEAM_ID, TOURNAMENT_ID, 1, 1, 0, 0, 3, 0, 3, 3))));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/standings", TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.standings[0].teamId").value(TEAM_ID));
    }

    @Test
    void getTournamentMatchAverages_deberiaDevolver200() throws Exception {
        when(statisticsService.getTournamentMatchAverages(TOURNAMENT_ID))
                .thenReturn(new TournamentMatchAveragesResponse(TOURNAMENT_ID, 5, 2.8, 9.1, 3.2));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/match-averages", TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageGoalsPerMatch").value(2.8));
    }

    @Test
    void getTournamentCardsTotal_deberiaDevolver200() throws Exception {
        when(statisticsService.getTournamentCardsTotal(TOURNAMENT_ID))
                .thenReturn(new CardsTotalResponse("tournament", TOURNAMENT_ID, 10, 2));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/cards", TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope").value("tournament"));
    }

    @Test
    void generateTournamentRecognitions_deberiaDevolver201() throws Exception {
        when(statisticsService.generateTournamentRecognitions(TOURNAMENT_ID))
                .thenReturn(new TournamentRecognitionResponse(TOURNAMENT_ID,
                        List.of(new TournamentRecognitionResponse.PlayerGoals(PLAYER_ID, 5)), 5,
                        List.of(new TournamentRecognitionResponse.TeamGoalsAgainst(TEAM_ID, 0)), 0,
                        LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/statistics/tournaments/{id}/recognitions", TOURNAMENT_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topScorersGoals").value(5));
    }

    @Test
    void getTournamentRecognitions_deberiaDevolver200() throws Exception {
        when(statisticsService.getTournamentRecognitions(TOURNAMENT_ID))
                .thenReturn(new TournamentRecognitionResponse(TOURNAMENT_ID,
                        List.of(new TournamentRecognitionResponse.PlayerGoals(PLAYER_ID, 5)), 5,
                        List.of(new TournamentRecognitionResponse.TeamGoalsAgainst(TEAM_ID, 0)), 0,
                        LocalDateTime.now()));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/recognitions", TOURNAMENT_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTournamentRecognitions_deberiaDevolver404SiNoSeHaGenerado() throws Exception {
        when(statisticsService.getTournamentRecognitions(TOURNAMENT_ID))
                .thenThrow(new RecognitionNotFoundException(TOURNAMENT_ID));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/recognitions", TOURNAMENT_ID))
                .andExpect(status().isNotFound());
    }

    // ---------- Partido ----------

    @Test
    void getMatchCardsTotal_deberiaDevolver200() throws Exception {
        when(statisticsService.getMatchCardsTotal(MATCH_ID))
                .thenReturn(new CardsTotalResponse("match", MATCH_ID, 1, 0));

        mockMvc.perform(get("/api/v1/statistics/matches/{id}/cards", MATCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scope").value("match"));
    }

    @Test
    void getMatchResult_deberiaDevolver200() throws Exception {
        when(statisticsService.getMatchResult(MATCH_ID))
                .thenReturn(new MatchResultResponse(MATCH_ID, TOURNAMENT_ID, List.of(
                        new MatchResultResponse.TeamResult(TEAM_ID, MatchResult.WON))));

        mockMvc.perform(get("/api/v1/statistics/matches/{id}/result", MATCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamResults[0].result").value("WON"));
    }
}
