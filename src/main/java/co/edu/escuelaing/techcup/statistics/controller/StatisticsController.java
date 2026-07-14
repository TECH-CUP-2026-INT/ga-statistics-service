package co.edu.escuelaing.techcup.statistics.controller;

import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerCardsResponse;
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
import co.edu.escuelaing.techcup.statistics.service.StatisticsService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Endpoint interno: lo consume el servicio de Competencia cuando
     * finaliza un partido, enviando el resumen de un jugador.
     */
    @PostMapping("/events")
    public ResponseEntity<Void> registerMatchStat(@Valid @RequestBody MatchStatEventRequest request) {
        statisticsService.registerMatchStat(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/players/{playerId}/average-win-rate")
    public ResponseEntity<PlayerAverageResponse> getAverageWinRate(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getAverageWinRate(playerId, tournamentId));
    }

    @GetMapping("/players/{playerId}/average-goals")
    public ResponseEntity<PlayerAverageResponse> getAverageGoals(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getAverageGoals(playerId, tournamentId));
    }

    @GetMapping("/players/{playerId}/average-fouls")
    public ResponseEntity<PlayerAverageResponse> getAverageFouls(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getAverageFouls(playerId, tournamentId));
    }

    @GetMapping("/players/{playerId}/average-minutes-played")
    public ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getAverageMinutesPlayed(playerId, tournamentId));
    }

    @GetMapping("/players/{playerId}/matches-played")
    public ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getMatchesPlayed(playerId, tournamentId));
    }

    /**
     * Ranking público. Ejemplo: /api/v1/statistics/rankings?type=GOALS&limit=10
     */
    @GetMapping("/rankings")
    public ResponseEntity<RankingResponse> getRanking(
            @RequestParam RankingType type,
            @RequestParam(required = false) Long tournamentId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statisticsService.getRanking(type, tournamentId, limit));
    }

    /**
     * Estadísticas generales del torneo: tabla de posiciones completa
     * (equipos, puntos, resultados).
     */
    @GetMapping("/tournaments/{tournamentId}/standings")
    public ResponseEntity<TournamentStandingsResponse> getTournamentStandings(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTournamentStandings(tournamentId));
    }

    /**
     * Estadísticas de un equipo dentro del torneo activo (el torneo activo
     * se resuelve internamente llamando al servicio de Torneos).
     */
    @GetMapping("/teams/{teamId}/statistics")
    public ResponseEntity<TeamStatisticsResponse> getTeamStatisticsInActiveTournament(
            @PathVariable Long teamId) {
        return ResponseEntity.ok(statisticsService.getTeamStatisticsInActiveTournament(teamId));
    }

    /**
     * Genera y GUARDA el reconocimiento del torneo. Lo llama el servicio de
     * Torneos al finalizar el torneo.
     */
    @PostMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(
            @PathVariable Long tournamentId) {
        TournamentRecognitionResponse response = statisticsService.generateTournamentRecognitions(tournamentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulta el reconocimiento ya generado (404 si el torneo no ha
     * finalizado / aún no se ha generado).
     */
    @GetMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTournamentRecognitions(tournamentId));
    }

    /**
     * Ranking de porteros por menos goles recibidos (valla menos vencida).
     */
    @GetMapping("/goalkeeper-ranking")
    public ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(
            @RequestParam(required = false) Long tournamentId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statisticsService.getGoalkeeperRanking(tournamentId, limit));
    }

    @GetMapping("/players/{playerId}/assists")
    public ResponseEntity<TotalResponse> getPlayerTotalAssists(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getPlayerTotalAssists(playerId, tournamentId));
    }

    // ---------- Jugador: totales y tarjetas ----------

    @GetMapping("/players/{playerId}/total-goals")
    public ResponseEntity<TotalResponse> getPlayerTotalGoals(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getPlayerTotalGoals(playerId, tournamentId));
    }

    @GetMapping("/players/{playerId}/total-fouls")
    public ResponseEntity<TotalResponse> getPlayerTotalFouls(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getPlayerTotalFouls(playerId, tournamentId));
    }

    @GetMapping("/players/{playerId}/cards")
    public ResponseEntity<PlayerCardsResponse> getPlayerCards(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getPlayerCards(playerId, tournamentId));
    }

    // ---------- Equipo ----------

    @GetMapping("/teams/{teamId}/match-record")
    public ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(
            @PathVariable Long teamId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTeamMatchRecord(teamId, tournamentId));
    }

    @GetMapping("/teams/{teamId}/average-goals")
    public ResponseEntity<TeamAverageResponse> getTeamAverageGoals(
            @PathVariable Long teamId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTeamAverageGoals(teamId, tournamentId));
    }

    @GetMapping("/teams/{teamId}/average-fouls")
    public ResponseEntity<TeamAverageResponse> getTeamAverageFouls(
            @PathVariable Long teamId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTeamAverageFouls(teamId, tournamentId));
    }

    @GetMapping("/teams/{teamId}/total-fouls")
    public ResponseEntity<TotalResponse> getTeamTotalFouls(
            @PathVariable Long teamId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTeamTotalFouls(teamId, tournamentId));
    }

    @GetMapping("/teams/{teamId}/goals")
    public ResponseEntity<TeamGoalsResponse> getTeamGoals(
            @PathVariable Long teamId,
            @RequestParam(required = false) Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTeamGoals(teamId, tournamentId));
    }

    // ---------- Torneo (agregados por partido) ----------

    @GetMapping("/tournaments/{tournamentId}/match-averages")
    public ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTournamentMatchAverages(tournamentId));
    }

    @GetMapping("/tournaments/{tournamentId}/cards")
    public ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTournamentCardsTotal(tournamentId));
    }

    // ---------- Partido ----------

    @GetMapping("/matches/{matchId}/cards")
    public ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable Long matchId) {
        return ResponseEntity.ok(statisticsService.getMatchCardsTotal(matchId));
    }

    @GetMapping("/matches/{matchId}/result")
    public ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable Long matchId) {
        return ResponseEntity.ok(statisticsService.getMatchResult(matchId));
    }
}
