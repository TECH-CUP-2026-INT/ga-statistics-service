package co.edu.escuelaing.techcup.statistics.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado GUARDADO del cálculo de reconocimientos de un torneo (máximo
 * goleador y malla menos vencida). Se genera una sola vez por torneo,
 * disparado por el servicio de Torneos al finalizarlo (POST), y luego se
 * consulta con GET sin volver a calcular.
 */
@Document(collection = "tournament_recognitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentRecognition {

    @Id
    private String id;

    @Indexed(unique = true)
    private String tournamentId;

    private List<String> topScorerPlayerIds;
    private long topScorersGoals;

    private List<String> bestDefenseTeamIds;
    private long bestDefenseGoalsAgainst;

    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
}
