package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Cliente Feign declarativo para el servicio de Torneos.
 * Spring implementa esta interfaz automáticamente.
 * <p>
 * Se activa con el perfil {@code feign}. Por defecto se usa
 * {@link TournamentClientImpl} con RestClient.
 */
@FeignClient(name = "tournament-service", url = "${services.tournaments.base-url}")
@Profile("feign")
public interface TournamentServiceFeignClient {

    @GetMapping("/tournaments/active")
    ActiveTournamentResponse getActiveTournament();
}
