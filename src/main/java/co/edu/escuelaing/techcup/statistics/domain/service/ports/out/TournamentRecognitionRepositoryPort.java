package co.edu.escuelaing.techcup.statistics.domain.service.ports.out;

import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;

import java.util.Optional;

public interface TournamentRecognitionRepositoryPort {

    Optional<TournamentRecognitionRecord> findByTournamentId(String tournamentId);

    /**
     * Guarda el reconocimiento. Si ya existia uno para el mismo torneo, lo
     * REEMPLAZA (el adaptador decide como resolver eso, la aplicacion no
     * necesita saber de ids de Mongo).
     */
    TournamentRecognitionRecord save(TournamentRecognitionRecord recognition);
}
