package co.edu.escuelaing.techcup.statistics.controller;

import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.RecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
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
     * Reconocimientos del torneo: máximo goleador y malla menos vencida.
     */
    @GetMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<RecognitionResponse> getTournamentRecognitions(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTournamentRecognitions(tournamentId));
    }
}
