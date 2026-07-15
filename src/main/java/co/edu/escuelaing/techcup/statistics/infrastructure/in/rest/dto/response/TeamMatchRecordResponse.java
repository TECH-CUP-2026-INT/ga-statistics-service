package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TeamMatchRecordResponse(
        String teamId, String tournamentId, long matchesPlayed, long wins, long draws, long losses,
        double winRatePercentage, double drawRatePercentage, double lossRatePercentage
) {}
