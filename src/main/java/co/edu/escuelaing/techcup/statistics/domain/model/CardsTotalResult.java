package co.edu.escuelaing.techcup.statistics.domain.model;

import java.util.UUID;

public record CardsTotalResult(
        String scope,
        UUID id,
        long yellowCards,
        long redCards
) {}
