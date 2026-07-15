package co.edu.escuelaing.techcup.statistics.domain;

public record CardsTotalResult(
        String scope,
        String id,
        long yellowCards,
        long redCards
) {
}
