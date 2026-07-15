package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;

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
    void getActiveTournamentId_deberiaDevolverElId() {
        RestClient rc = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(rc.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenReturn(new ActiveTournamentResponse("tournament-5"));
        TournamentClient client = new TournamentClientImpl(rc);
        assertThat(client.getActiveTournamentId()).isEqualTo("tournament-5");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiEsNulo() {
        RestClient rc = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(rc.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenReturn(new ActiveTournamentResponse(null));
        TournamentClient client = new TournamentClientImpl(rc);
        assertThrows(ExternalServiceException.class, client::getActiveTournamentId);
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiFallaHttp() {
        RestClient rc = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        when(rc.get().uri(ACTIVE_PATH).retrieve().body(ActiveTournamentResponse.class))
                .thenThrow(new RestClientException("timeout"));
        TournamentClient client = new TournamentClientImpl(rc);
        assertThrows(ExternalServiceException.class, client::getActiveTournamentId);
    }
}
