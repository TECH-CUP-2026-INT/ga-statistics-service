package co.edu.escuelaing.techcup.statistics.client;

import co.edu.escuelaing.techcup.statistics.exception.ExternalServiceException;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TournamentClientImplTest {

    private static final String ACTIVE_PATH = "/tournaments/active";

    @Test
    void getActiveTournamentId_deberiaDevolverElIdCuandoLaRespuestaEsValida() {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(restClient.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenReturn(new ActiveTournamentResponse("tournament-5"));

        TournamentClient client = new TournamentClientImpl(restClient);

        String result = client.getActiveTournamentId();

        assertThat(result).isEqualTo("tournament-5");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiLaRespuestaEsNula() {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(restClient.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenReturn(null);

        TournamentClient client = new TournamentClientImpl(restClient);

        assertThrows(ExternalServiceException.class, client::getActiveTournamentId);
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiElIdEsNulo() {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(restClient.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenReturn(new ActiveTournamentResponse(null));

        TournamentClient client = new TournamentClientImpl(restClient);

        assertThrows(ExternalServiceException.class, client::getActiveTournamentId);
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiFallaLaLlamadaHttp() {
        RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(restClient.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenThrow(new RestClientException("timeout"));

        TournamentClient client = new TournamentClientImpl(restClient);

        assertThrows(ExternalServiceException.class, client::getActiveTournamentId);
    }
}
