package co.edu.escuelaing.techcup.statistics.application.mapper;

import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.RankingEntryResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.RankingResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TotalResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.domain.model.CardsTotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.GoalkeeperRankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResultResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchesPlayedResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerCardsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamGoalsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamMatchRecordResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamStatisticsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentMatchAveragesResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentStandingsResult;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Convierte los objetos de DOMINIO (calculados por la aplicacion) hacia los
 * DTOs de respuesta de la web. Solo lo usa el controller.
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

    default TournamentRecognitionResponse toResponse(TournamentRecognitionRecord domain) {
        List<TournamentRecognitionResponse.PlayerGoals> topScorers = domain.getTopScorerPlayerIds().stream()
                .map(id -> new TournamentRecognitionResponse.PlayerGoals(id, domain.getTopScorersGoals()))
                .toList();

        List<TournamentRecognitionResponse.TeamGoalsAgainst> bestDefenses = domain.getBestDefenseTeamIds()
                .stream()
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
