package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Objeto de DOMINIO del reconocimiento de un torneo (mÃ¡ximo goleador y
 * malla menos vencida). Sin anotaciones de persistencia; la conversiÃ³n
 * hacia/desde el documento de MongoDB la realiza TournamentRecognitionMapper.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentRecognitionRecord {

    private String id;
    private UUID tournamentId;

    private List<UUID> topScorerPlayerIds;
    private long topScorersGoals;
    private List<UUID> bestDefenseTeamIds;
    private long bestDefenseGoalsAgainst;

    private LocalDateTime generatedAt;
}
