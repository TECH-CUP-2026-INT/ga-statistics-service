package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record TeamMatchRecordResponse(
        UUID teamId, UUID tournamentId, long matchesPlayed, long wins, long draws, long losses,
        double winRatePercentage, double drawRatePercentage, double lossRatePercentage
) {}
