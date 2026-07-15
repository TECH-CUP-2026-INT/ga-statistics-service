package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller;
import java.util.UUID;
import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.application.mapper.StatisticsResponseMapper;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingType;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller.swagger.StatisticsSwagger;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController implements StatisticsSwagger {

    private final StatisticsUseCase statisticsUseCase;
    private final PlayerMatchStatMapper playerMatchStatMapper;
    private final StatisticsResponseMapper responseMapper;

    @Override
    public ResponseEntity<Void> registerMatchStat(@Valid @RequestBody MatchStatEventRequest request) {
        statisticsUseCase.registerMatchStat(playerMatchStatMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override public ResponseEntity<PlayerAverageResponse> getAverageWinRate(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageWinRate(playerId, tournamentId)));
    }

    @Override public ResponseEntity<PlayerAverageResponse> getAverageGoals(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageGoals(playerId, tournamentId)));
    }

    @Override public ResponseEntity<PlayerAverageResponse> getAverageFouls(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageFouls(playerId, tournamentId)));
    }

    @Override public ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageMinutesPlayed(playerId, tournamentId)));
    }

    @Override public ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchesPlayed(playerId, tournamentId)));
    }

    @Override public ResponseEntity<TotalResponse> getPlayerTotalGoals(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalGoals(playerId, tournamentId)));
    }

    @Override public ResponseEntity<TotalResponse> getPlayerTotalFouls(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalFouls(playerId, tournamentId)));
    }

    @Override public ResponseEntity<TotalResponse> getPlayerTotalAssists(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalAssists(playerId, tournamentId)));
    }

    @Override public ResponseEntity<PlayerCardsResponse> getPlayerCards(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerCards(playerId, tournamentId)));
    }

    @Override public ResponseEntity<TeamStatisticsResponse> getTeamStatistics(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamStatistics(teamId, tournamentId)));
    }

    @Override public ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamMatchRecord(teamId, tournamentId)));
    }

    @Override public ResponseEntity<TeamAverageResponse> getTeamAverageGoals(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamAverageGoals(teamId, tournamentId)));
    }

    @Override public ResponseEntity<TeamAverageResponse> getTeamAverageFouls(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamAverageFouls(teamId, tournamentId)));
    }

    @Override public ResponseEntity<TotalResponse> getTeamTotalFouls(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamTotalFouls(teamId, tournamentId)));
    }

    @Override public ResponseEntity<TeamGoalsResponse> getTeamGoals(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamGoals(teamId, tournamentId)));
    }

    @Override public ResponseEntity<TournamentStandingsResponse> getTournamentStandings(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentStandings(tournamentId)));
    }

    @Override public ResponseEntity<RankingResponse> getRanking(@RequestParam String type, @RequestParam(required = false) UUID tournamentId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getRanking(RankingType.valueOf(type), tournamentId, limit)));
    }

    @Override public ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(@RequestParam(required = false) UUID tournamentId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getGoalkeeperRanking(tournamentId, limit)));
    }

    @Override public ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentMatchAverages(tournamentId)));
    }

    @Override public ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentCardsTotal(tournamentId)));
    }

    @Override public ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(@PathVariable UUID tournamentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(statisticsUseCase.generateTournamentRecognitions(tournamentId)));
    }

    @Override public ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentRecognitions(tournamentId)));
    }

    @Override public ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable UUID matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchCardsTotal(matchId)));
    }

    @Override public ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable UUID matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchResult(matchId)));
    }
}
