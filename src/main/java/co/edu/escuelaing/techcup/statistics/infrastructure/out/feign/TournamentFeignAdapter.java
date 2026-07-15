package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import feign.FeignException;

/**
 * Adaptador que implementa {@link TournamentClient} usando Feign.
 * Reemplaza a {@link TournamentClientImpl} cuando el perfil {@code feign} está activo.
 */
@Component
@Profile("feign")
public class TournamentFeignAdapter implements TournamentClient {

    private final TournamentServiceFeignClient feignClient;

    public TournamentFeignAdapter(TournamentServiceFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public String getActiveTournamentId() {
        try {
            ActiveTournamentResponse response = feignClient.getActiveTournament();

            if (response == null || response.id() == null || response.id().isBlank()) {
                throw new ExternalServiceException(
                        "El servicio de Torneos respondió sin un torneo activo válido.");
            }
            return response.id();
        } catch (FeignException ex) {
            throw new ExternalServiceException(
                    "No fue posible consultar el torneo activo en el servicio de Torneos: "
                            + ex.getMessage());
        }
    }
}
