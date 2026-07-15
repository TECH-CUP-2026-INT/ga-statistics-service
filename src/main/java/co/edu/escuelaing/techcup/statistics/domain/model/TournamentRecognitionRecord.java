package co.edu.escuelaing.techcup.statistics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Objeto de DOMINIO del reconocimiento de un torneo (máximo goleador y
 * malla menos vencida). Sin anotaciones de persistencia; la conversión
 * hacia/desde el documento de MongoDB la realiza TournamentRecognitionMapper.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentRecognitionRecord {

    private String id;
    private String tournamentId;

    private List<String> topScorerPlayerIds;
    private long topScorersGoals;

    private List<String> bestDefenseTeamIds;
    private long bestDefenseGoalsAgainst;

    private LocalDateTime generatedAt;
}
