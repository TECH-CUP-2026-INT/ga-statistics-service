package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record TeamMatchRecordResult(
        UUID teamId,
        UUID tournamentId,
        long matchesPlayed,
        long wins,
        long draws,
        long losses,
        double winRatePercentage,
        double drawRatePercentage,
        double lossRatePercentage
) {}
