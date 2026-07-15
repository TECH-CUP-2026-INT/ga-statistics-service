package co.edu.escuelaing.techcup.statistics.application.mapper;

import co.edu.escuelaing.techcup.statistics.domain.model.*;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.*;

import org.mapstruct.Mapper;

import java.util.List;

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
        List<TournamentRecognitionResponse.TeamGoalsAgainst> defenses = domain.getBestDefenseTeamIds().stream()
                .map(id -> new TournamentRecognitionResponse.TeamGoalsAgainst(id, domain.getBestDefenseGoalsAgainst()))
                .toList();
        return new TournamentRecognitionResponse(domain.getTournamentId(), topScorers,
                domain.getTopScorersGoals(), defenses, domain.getBestDefenseGoalsAgainst(), domain.getGeneratedAt());
    }
}
