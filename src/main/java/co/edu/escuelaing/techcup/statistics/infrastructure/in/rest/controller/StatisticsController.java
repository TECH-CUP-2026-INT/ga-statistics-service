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

    @PostMapping("/events")
    @Override
    public ResponseEntity<Void> registerMatchStat(@Valid @RequestBody MatchStatEventRequest request) {
        statisticsUseCase.registerMatchStat(playerMatchStatMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/players/{playerId}/average-win-rate")
    @Override public ResponseEntity<PlayerAverageResponse> getAverageWinRate(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageWinRate(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/average-goals")
    @Override public ResponseEntity<PlayerAverageResponse> getAverageGoals(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageGoals(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/average-fouls")
    @Override public ResponseEntity<PlayerAverageResponse> getAverageFouls(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageFouls(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/average-minutes-played")
    @Override public ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageMinutesPlayed(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/matches-played")
    @Override public ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchesPlayed(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/total-goals")
    @Override public ResponseEntity<TotalResponse> getPlayerTotalGoals(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalGoals(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/total-fouls")
    @Override public ResponseEntity<TotalResponse> getPlayerTotalFouls(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalFouls(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/assists")
    @Override public ResponseEntity<TotalResponse> getPlayerTotalAssists(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalAssists(playerId, tournamentId)));
    }

    @GetMapping("/players/{playerId}/cards")
    @Override public ResponseEntity<PlayerCardsResponse> getPlayerCards(@PathVariable("playerId") UUID playerId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerCards(playerId, tournamentId)));
    }

    @GetMapping("/teams/{teamId}/statistics")
    @Override public ResponseEntity<TeamStatisticsResponse> getTeamStatistics(@PathVariable("teamId") UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamStatistics(teamId, tournamentId)));
    }

    @GetMapping("/teams/{teamId}/match-record")
    @Override public ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(@PathVariable("teamId") UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamMatchRecord(teamId, tournamentId)));
    }

    @GetMapping("/teams/{teamId}/average-goals")
    @Override public ResponseEntity<TeamAverageResponse> getTeamAverageGoals(@PathVariable("teamId") UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamAverageGoals(teamId, tournamentId)));
    }

    @GetMapping("/teams/{teamId}/average-fouls")
    @Override public ResponseEntity<TeamAverageResponse> getTeamAverageFouls(@PathVariable("teamId") UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamAverageFouls(teamId, tournamentId)));
    }

    @GetMapping("/teams/{teamId}/total-fouls")
    @Override public ResponseEntity<TotalResponse> getTeamTotalFouls(@PathVariable("teamId") UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamTotalFouls(teamId, tournamentId)));
    }

    @GetMapping("/teams/{teamId}/goals")
    @Override public ResponseEntity<TeamGoalsResponse> getTeamGoals(@PathVariable("teamId") UUID teamId, @RequestParam(required = false) UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamGoals(teamId, tournamentId)));
    }

    @GetMapping("/tournaments/{tournamentId}/standings")
    @Override public ResponseEntity<TournamentStandingsResponse> getTournamentStandings(@PathVariable("tournamentId") UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentStandings(tournamentId)));
    }

    @GetMapping("/rankings")
    @Override public ResponseEntity<RankingResponse> getRanking(@RequestParam String type, @RequestParam(required = false) UUID tournamentId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getRanking(RankingType.valueOf(type), tournamentId, limit)));
    }

    @GetMapping("/goalkeeper-ranking")
    @Override public ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(@RequestParam(required = false) UUID tournamentId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getGoalkeeperRanking(tournamentId, limit)));
    }

    @GetMapping("/tournaments/{tournamentId}/match-averages")
    @Override public ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(@PathVariable("tournamentId") UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentMatchAverages(tournamentId)));
    }

    @GetMapping("/tournaments/{tournamentId}/cards")
    @Override public ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(@PathVariable("tournamentId") UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentCardsTotal(tournamentId)));
    }

    @PostMapping("/tournaments/{tournamentId}/recognitions")
    @Override public ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(@PathVariable("tournamentId") UUID tournamentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(statisticsUseCase.generateTournamentRecognitions(tournamentId)));
    }

    @GetMapping("/tournaments/{tournamentId}/recognitions")
    @Override public ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(@PathVariable("tournamentId") UUID tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentRecognitions(tournamentId)));
    }

    @GetMapping("/matches/{matchId}/cards")
    @Override public ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable("matchId") UUID matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchCardsTotal(matchId)));
    }

    @GetMapping("/matches/{matchId}/result")
    @Override public ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable("matchId") UUID matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchResult(matchId)));
    }
}
