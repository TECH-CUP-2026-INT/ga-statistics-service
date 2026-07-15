package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de finalización de torneo.
 * Escucha {@link TournamentFinalizedEvent} y genera reconocimientos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentEventConsumer {

    private final StatisticsUseCase statisticsUseCase;

    @RabbitListener(queues = "${rabbitmq.queue.tournament-events}")
    public void handleTournamentFinalized(TournamentFinalizedEvent event) {
        log.info("Torneo finalizado recibido: tournamentId={}", event.tournamentId());
        try {
            var recognition = statisticsUseCase.generateTournamentRecognitions(event.tournamentId());
            log.info("Reconocimientos generados para torneo '{}': {} goleador(es), {} mejor(es) defensa",
                    event.tournamentId(),
                    recognition.getTopScorerPlayerIds().size(),
                    recognition.getBestDefenseTeamIds().size());
        } catch (Exception e) {
            log.error("Error generando reconocimientos desde RabbitMQ para torneo '{}': {}",
                    event.tournamentId(), e.getMessage());
        }
    }
}
