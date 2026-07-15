package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Profile("!feign")
public class TournamentClientImpl implements TournamentClient {

    private final RestClient restClient;

    private static final String ACTIVE_TOURNAMENT_PATH = "/tournaments/active";

    @Autowired
    public TournamentClientImpl(@Value("${services.tournaments.base-url}") String baseUrl) {
        this(RestClient.builder().baseUrl(baseUrl).build());
    }

    /** Constructor usado por Spring y por los tests para inyectar un RestClient simulado. */
    TournamentClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String getActiveTournamentId() {
        try {
            ActiveTournamentResponse response = restClient.get()
                    .uri(ACTIVE_TOURNAMENT_PATH)
                    .retrieve()
                    .body(ActiveTournamentResponse.class);

            if (response == null || response.id() == null || response.id().isBlank()) {
                throw new ExternalServiceException(
                        "El servicio de Torneos respondió sin un torneo activo válido.");
            }
            return response.id();
        } catch (RestClientException ex) {
            throw new ExternalServiceException(
                    "No fue posible consultar el torneo activo en el servicio de Torneos: "
                            + ex.getMessage());
        }
    }
}
