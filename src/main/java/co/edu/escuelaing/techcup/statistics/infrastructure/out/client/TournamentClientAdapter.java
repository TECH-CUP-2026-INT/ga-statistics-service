package co.edu.escuelaing.techcup.statistics.infrastructure.out.client;

import co.edu.escuelaing.techcup.statistics.domain.exception.ExternalServiceException;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.TournamentClientPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Adaptador de salida: implementa el puerto hacia Torneos usando HTTP
 * (RestClient). Es el UNICO lugar que conoce la ruta real y el formato de
 * respuesta del servicio de Torneos.
 */
@Component
public class TournamentClientAdapter implements TournamentClientPort {

    private final RestClient restClient;

    /**
     * Ruta relativa asumida del servicio de Torneos. AJUSTAR cuando el
     * equipo de Torneos confirme/agregue este endpoint.
     */
    private static final String ACTIVE_TOURNAMENT_PATH = "/tournaments/active";

    @Autowired
    public TournamentClientAdapter(@Value("${services.tournaments.base-url}") String baseUrl) {
        this(RestClient.builder().baseUrl(baseUrl).build());
    }

    /** Constructor usado por Spring en produccion y por los tests para inyectar un RestClient simulado. */
    TournamentClientAdapter(RestClient restClient) {
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
