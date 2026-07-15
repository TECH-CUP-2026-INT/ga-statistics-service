package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest;

import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapperImpl;
import co.edu.escuelaing.techcup.statistics.application.mapper.StatisticsResponseMapperImpl;
import co.edu.escuelaing.techcup.statistics.domain.model.*;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller.StatisticsController;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = StatisticsController.class, excludeAutoConfiguration = {RabbitAutoConfiguration.class})
@Import({PlayerMatchStatMapperImpl.class, StatisticsResponseMapperImpl.class})
class StatisticsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private StatisticsUseCase statisticsUseCase;

    private static final UUID PID = UUID.randomUUID();
    private static final UUID TID = UUID.randomUUID();
    private static final UUID TNID = UUID.randomUUID();
    private static final UUID MID = UUID.randomUUID();

    // ======================== registerMatchStat ========================

    @Test
    void registerMatchStat_deberiaDevolver201() throws Exception {
        var req = new MatchStatEventRequest(PID, TID, MID, TNID, MatchResult.WON, 2, 1, 0, 3, 90, 1, false);
        mockMvc.perform(post("/api/v1/statistics/events").contentType("application/json")
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());
    }

    @Test
    void registerMatchStat_deberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/v1/statistics/events").contentType("application/json")
                .content("{\"teamId\": \"t\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void registerMatchStat_deberiaDevolver409() throws Exception {
        var req = new MatchStatEventRequest(PID, TID, MID, TNID, MatchResult.WON, 2, 0, 0, 1, 90, 0, false);
        doThrow(new DuplicateMatchStatException(PID, MID)).when(statisticsUseCase).registerMatchStat(any());
        mockMvc.perform(post("/api/v1/statistics/events").contentType("application/json")
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict());
    }

    // ======================== Player averages ========================

    @Test
    void getAverageWinRate_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageWinRate(eq(PID), any()))
                .thenReturn(new PlayerAverageResult(PID, TNID, "avg", 50.0, 2));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-win-rate", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(50.0));
    }

    @Test
    void getAverageGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageGoals(eq(PID), any()))
                .thenReturn(new PlayerAverageResult(PID, TNID, "averageGoals", 1.67, 3));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-goals", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(1.67));
    }

    @Test
    void getAverageFouls_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageFouls(eq(PID), any()))
                .thenReturn(new PlayerAverageResult(PID, TNID, "averageFouls", 2.5, 4));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-fouls", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(2.5));
    }

    @Test
    void getAverageMinutesPlayed_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageMinutesPlayed(eq(PID), any()))
                .thenReturn(new PlayerAverageResult(PID, TNID, "averageMinutesPlayed", 67.5, 2));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-minutes-played", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(67.5));
    }

    @Test
    void getMatchesPlayed_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchesPlayed(eq(PID), any()))
                .thenReturn(new MatchesPlayedResult(PID, TNID, 5));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/matches-played", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.matchesPlayed").value(5));
    }

    // ======================== Player totals ========================

    @Test
    void getPlayerTotalGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerTotalGoals(eq(PID), any()))
                .thenReturn(new TotalResult(PID, TNID, "totalGoals", 10, 3));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/total-goals", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(10));
    }

    @Test
    void getPlayerTotalFouls_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerTotalFouls(eq(PID), any()))
                .thenReturn(new TotalResult(PID, TNID, "totalFouls", 8, 3));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/total-fouls", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(8));
    }

    @Test
    void getPlayerTotalAssists_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerTotalAssists(eq(PID), any()))
                .thenReturn(new TotalResult(PID, TNID, "totalAssists", 5, 3));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/assists", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(5));
    }

    @Test
    void getPlayerCards_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerCards(eq(PID), any()))
                .thenReturn(new PlayerCardsResult(PID, TNID, 3, 1));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/cards", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.yellowCards").value(3))
                .andExpect(jsonPath("$.redCards").value(1));
    }

    // ======================== Team endpoints ========================

    @Test
    void getTeamStatistics_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamStatistics(any(), any()))
                .thenReturn(new TeamStatisticsResult(TID, TNID, 2, 1, 0, 1, 3, 2, 1, 3));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.points").value(3));
    }

    @Test
    void getTeamStatistics_deberiaDevolver502() throws Exception {
        when(statisticsUseCase.getTeamStatistics(any(), any()))
                .thenThrow(new ExternalServiceException("error"));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getTeamMatchRecord_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamMatchRecord(eq(TID), any()))
                .thenReturn(new TeamMatchRecordResult(TID, TNID, 10, 5, 2, 3, 50.0, 20.0, 30.0));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/match-record", TID))
                .andExpect(status().isOk())        .andExpect(jsonPath("$.matchesPlayed").value(10))
                .andExpect(jsonPath("$.wins").value(5));
    }

    @Test
    void getTeamAverageGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamAverageGoals(eq(TID), any()))
                .thenReturn(new TeamAverageResult(TID, TNID, "averageGoalsPerMatch", 2.5, 4));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/average-goals", TID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(2.5));
    }

    @Test
    void getTeamAverageFouls_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamAverageFouls(eq(TID), any()))
                .thenReturn(new TeamAverageResult(TID, TNID, "averageFoulsPerMatch", 8.0, 4));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/average-fouls", TID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(8.0));
    }

    @Test
    void getTeamTotalFouls_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamTotalFouls(eq(TID), any()))
                .thenReturn(new TotalResult(TID, TNID, "totalFouls", 32, 4));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/total-fouls", TID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(32));
    }

    @Test
    void getTeamGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamGoals(eq(TID), any()))
                .thenReturn(new TeamGoalsResult(TID, TNID, 15, 5, 10));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/goals", TID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.goalsFor").value(15))
                .andExpect(jsonPath("$.goalDifference").value(10));
    }

    // ======================== Tournament endpoints ========================

    @Test
    void getTournamentStandings_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentStandings(TNID))
                .thenReturn(new TournamentStandingsResult(TNID, List.of(
                        new TeamStatisticsResult(TID, TNID, 1, 1, 0, 0, 3, 0, 3, 3))));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/standings", TNID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.standings[0].teamId").value(TID));
    }

    @Test
    void getTournamentMatchAverages_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentMatchAverages(TNID))
                .thenReturn(new TournamentMatchAveragesResult(TNID, 5, 3.5, 12.0, 2.0));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/match-averages", TNID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.averageGoalsPerMatch").value(3.5));
    }

    @Test
    void getTournamentCardsTotal_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentCardsTotal(TNID))
                .thenReturn(new CardsTotalResult("tournament", TNID, 20, 3));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/cards", TNID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.yellowCards").value(20))
                .andExpect(jsonPath("$.redCards").value(3));
    }

    @Test
    void getMatchCardsTotal_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchCardsTotal(MID))
                .thenReturn(new CardsTotalResult("match", MID, 4, 1));
        mockMvc.perform(get("/api/v1/statistics/matches/{id}/cards", MID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.yellowCards").value(4));
    }

    @Test
    void getMatchResult_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchResult(MID))
                .thenReturn(new MatchResultResult(MID, TNID, List.of(
                        new MatchResultResult.TeamResultEntry(TID, MatchResult.WON))));
        mockMvc.perform(get("/api/v1/statistics/matches/{id}/result", MID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.teamResults[0].result").value("WON"));
    }

    // ======================== Ranking ========================

    @Test
    void getRanking_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getRanking(eq(RankingType.GOALS), any(), eq(10)))
                .thenReturn(new RankingResult("GOALS", null, List.of(new RankingResult.RankingEntry(1, PID, 5))));
        mockMvc.perform(get("/api/v1/statistics/rankings").param("type", "GOALS"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.entries[0].playerId").value(PID));
    }

    @Test
    void getRanking_conTournamentId_deberiaPasarParametro() throws Exception {
        when(statisticsUseCase.getRanking(eq(RankingType.GOALS), eq(TNID), eq(10)))
                .thenReturn(new RankingResult("GOALS", TNID, List.of(new RankingResult.RankingEntry(1, PID, 5))));
        mockMvc.perform(get("/api/v1/statistics/rankings")
                        .param("type", "GOALS").param("tournamentId", TNID.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.entries[0].playerId").value(PID));
    }

    @Test
    void getRanking_fouls_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getRanking(eq(RankingType.FOULS), any(), eq(10)))
                .thenReturn(new RankingResult("FOULS", null, List.of(new RankingResult.RankingEntry(1, PID, 12))));
        mockMvc.perform(get("/api/v1/statistics/rankings").param("type", "FOULS"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.entries[0].value").value(12));
    }

    @Test
    void getRanking_tipoInvalido_deberiaDevolver400() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/rankings").param("type", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getGoalkeeperRanking_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getGoalkeeperRanking(any(), eq(10)))
                .thenReturn(new GoalkeeperRankingResult(TNID, List.of(
                        new GoalkeeperRankingResult.GoalkeeperEntry(1, UUID.randomUUID(), 3))));
        mockMvc.perform(get("/api/v1/statistics/goalkeeper-ranking"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.entries[0].playerId").isString());
    }

    // ======================== Recognitions ========================

    @Test
    void generateTournamentRecognitions_deberiaDevolver201() throws Exception {
        when(statisticsUseCase.generateTournamentRecognitions(TNID))
                .thenReturn(TournamentRecognitionRecord.builder()
                        .tournamentId(TNID)
                        .topScorerPlayerIds(List.of(PID)).topScorersGoals(5)
                        .bestDefenseTeamIds(List.of(TID)).bestDefenseGoalsAgainst(0)
                        .generatedAt(LocalDateTime.now()).build());
        mockMvc.perform(post("/api/v1/statistics/tournaments/{id}/recognitions", TNID))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.topScorersGoals").value(5));
    }

    @Test
    void getTournamentRecognitions_deberiaDevolver404() throws Exception {
        when(statisticsUseCase.getTournamentRecognitions(TNID))
                .thenThrow(new RecognitionNotFoundException(TNID));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/recognitions", TNID))
                .andExpect(status().isNotFound());
    }

    // ======================== Error cases ========================

    @Test
    void getAverageWinRate_deberiaDevolver502() throws Exception {
        when(statisticsUseCase.getAverageWinRate(eq(PID), any()))
                .thenThrow(new ExternalServiceException("error"));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-win-rate", PID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getAverageGoals_deberiaDevolver502() throws Exception {
        when(statisticsUseCase.getAverageGoals(eq(PID), any()))
                .thenThrow(new ExternalServiceException("error"));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-goals", PID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getTournamentStandings_deberiaDevolver502() throws Exception {
        when(statisticsUseCase.getTournamentStandings(TNID))
                .thenThrow(new ExternalServiceException("error"));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/standings", TNID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getGoalkeeperRanking_conTournamentId_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getGoalkeeperRanking(eq(TNID), eq(5)))
                .thenReturn(new GoalkeeperRankingResult(TNID, List.of()));
        mockMvc.perform(get("/api/v1/statistics/goalkeeper-ranking")
                        .param("tournamentId", TNID.toString()).param("limit", "5"))
                .andExpect(status().isOk());
    }
}
