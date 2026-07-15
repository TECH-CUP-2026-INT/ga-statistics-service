package co.edu.escuelaing.techcup.statistics.domain;

public record TeamMatchRecordResult(
        String teamId,
        String tournamentId,
        long matchesPlayed,
        long wins,
        long draws,
        long losses,
        double winRatePercentage,
        double drawRatePercentage,
        double lossRatePercentage
) {
}
