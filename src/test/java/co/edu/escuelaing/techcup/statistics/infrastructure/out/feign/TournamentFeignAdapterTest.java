package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TournamentFeignAdapterTest {

    private TournamentServiceFeignClient feignClient;
    private TournamentFeignAdapter adapter;

    @BeforeEach
    void setUp() {
        feignClient = mock(TournamentServiceFeignClient.class);
        adapter = new TournamentFeignAdapter(feignClient);
    }

    @Test
    void getActiveTournamentId_deberiaRetornarId() {
        when(feignClient.getActiveTournament()).thenReturn(new ActiveTournamentResponse("torneo-1"));
        assertThat(adapter.getActiveTournamentId()).isEqualTo("torneo-1");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiRespuestaEsNull() {
        when(feignClient.getActiveTournament()).thenReturn(null);
        assertThrows(ExternalServiceException.class, () -> adapter.getActiveTournamentId());
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiIdEsNull() {
        when(feignClient.getActiveTournament()).thenReturn(new ActiveTournamentResponse(null));
        assertThrows(ExternalServiceException.class, () -> adapter.getActiveTournamentId());
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiIdEsVacio() {
        when(feignClient.getActiveTournament()).thenReturn(new ActiveTournamentResponse(""));
        assertThrows(ExternalServiceException.class, () -> adapter.getActiveTournamentId());
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionSiFeignFalla() {
        when(feignClient.getActiveTournament()).thenThrow(mock(FeignException.class));
        assertThrows(ExternalServiceException.class, () -> adapter.getActiveTournamentId());
    }
}
