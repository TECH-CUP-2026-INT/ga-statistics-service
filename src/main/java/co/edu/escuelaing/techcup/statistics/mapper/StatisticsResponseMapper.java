package co.edu.escuelaing.techcup.statistics.mapper;

import co.edu.escuelaing.techcup.statistics.domain.CardsTotalResult;
import co.edu.escuelaing.techcup.statistics.domain.GoalkeeperRankingResult;
import co.edu.escuelaing.techcup.statistics.domain.MatchResultResult;
import co.edu.escuelaing.techcup.statistics.domain.MatchesPlayedResult;
import co.edu.escuelaing.techcup.statistics.domain.PlayerAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.PlayerCardsResult;
import co.edu.escuelaing.techcup.statistics.domain.RankingResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamGoalsResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamMatchRecordResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamStatisticsResult;
import co.edu.escuelaing.techcup.statistics.domain.TotalResult;
import co.edu.escuelaing.techcup.statistics.domain.TournamentMatchAveragesResult;
import co.edu.escuelaing.techcup.statistics.domain.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.domain.TournamentStandingsResult;
import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingEntryResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Convierte los objetos de DOMINIO (calculados por StatisticsServiceImpl)
 * hacia los DTOs de respuesta de la capa web. El service NUNCA construye un
 * DTO de respuesta directamente -- siempre devuelve dominio, y es el
 * controller quien llama a este mapper antes de responder.
 */
@Mapper(componentModel = "spring")
public interface StatisticsResponseMapper {

    PlayerAverageResponse toResponse(PlayerAverageResult result);

    MatchesPlayedResponse toResponse(MatchesPlayedResult result);

    TotalResponse toResponse(TotalResult result);

    PlayerCardsResponse toResponse(PlayerCardsResult result);

    RankingEntryResponse toResponse(RankingResult.RankingEntry entry);

    RankingResponse toResponse(RankingResult result);

    TeamStatisticsResponse toResponse(TeamStatisticsResult result);

    TeamMatchRecordResponse toResponse(TeamMatchRecordResult result);

    TeamAverageResponse toResponse(TeamAverageResult result);

    TeamGoalsResponse toResponse(TeamGoalsResult result);

    TournamentStandingsResponse toResponse(TournamentStandingsResult result);

    TournamentMatchAveragesResponse toResponse(TournamentMatchAveragesResult result);

    CardsTotalResponse toResponse(CardsTotalResult result);

    MatchResultResponse.TeamResult toResponse(MatchResultResult.TeamResultEntry entry);

    MatchResultResponse toResponse(MatchResultResult result);

    GoalkeeperRankingResponse.Entry toResponse(GoalkeeperRankingResult.GoalkeeperEntry entry);

    GoalkeeperRankingResponse toResponse(GoalkeeperRankingResult result);

    /**
     * Caso especial: el dominio guarda listas de ids + un valor compartido
     * (topScorerPlayerIds + topScorersGoals), pero el DTO necesita una
     * lista de pares (playerId, goals). No es un mapeo campo a campo
     * directo, por eso es un metodo "default" dentro del mapper (sigue
     * siendo responsabilidad de esta clase, no del service).
     */
    default TournamentRecognitionResponse toResponse(TournamentRecognitionRecord domain) {
        List<TournamentRecognitionResponse.PlayerGoals> topScorers = domain.getTopScorerPlayerIds().stream()
                .map(id -> new TournamentRecognitionResponse.PlayerGoals(id, domain.getTopScorersGoals()))
                .toList();

        List<TournamentRecognitionResponse.TeamGoalsAgainst> bestDefenses = domain.getBestDefenseTeamIds().stream()
                .map(id -> new TournamentRecognitionResponse.TeamGoalsAgainst(
                        id, domain.getBestDefenseGoalsAgainst()))
                .toList();

        return new TournamentRecognitionResponse(
                domain.getTournamentId(),
                topScorers,
                domain.getTopScorersGoals(),
                bestDefenses,
                domain.getBestDefenseGoalsAgainst(),
                domain.getGeneratedAt());
    }
}
