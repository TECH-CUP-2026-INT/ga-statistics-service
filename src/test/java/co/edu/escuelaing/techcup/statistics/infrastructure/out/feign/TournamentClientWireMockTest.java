package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba de integración con WireMock para el cliente HTTP de Torneos.
 * Levanta un servidor HTTP simulado que emula el comportamiento del
 * servicio de Torneos, probando todos los escenarios:
 * <ul>
 *   <li>Respuesta exitosa con torneo activo</li>
 *   <li>Respuesta 404 (sin torneo activo)</li>
 *   <li>Respuesta con body inválido</li>
 *   <li>Timeout en la conexión</li>
 *   <li>Servidor caído</li>
 * </ul>
 */
@WireMockTest(httpPort = 9090)
class TournamentClientWireMockTest {

    private TournamentClient client;

    @BeforeEach
    void setUp() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9090")
                .build();
        client = new TournamentClientImpl(restClient, "/tournaments/active");
    }

    @Test
    void getActiveTournamentId_deberiaRetornarIdCuandoElServicioRespondeOk() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"tournament-2026-01\"}")));

        String result = client.getActiveTournamentId();

        assertThat(result).isEqualTo("tournament-2026-01");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionCuandoElServicioResponde404() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse().withStatus(404)));

        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> client.getActiveTournamentId());

        assertThat(ex.getMessage()).contains("No fue posible consultar el torneo activo");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionCuandoElBodyEsNull() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("null")));

        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> client.getActiveTournamentId());

        assertThat(ex.getMessage()).contains("respondió sin un torneo activo válido");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionCuandoElIdEsVacio() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"\"}")));

        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> client.getActiveTournamentId());

        assertThat(ex.getMessage()).contains("respondió sin un torneo activo válido");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionCuandoElBodyEsInvalido() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{json mal formado}")));

        assertThrows(ExternalServiceException.class,
                () -> client.getActiveTournamentId());
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionCuandoElServicioTardaDemasiado() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(5000) // 5 segundos de delay
                        .withBody("{\"id\": \"tournament-x\"}")));

        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> client.getActiveTournamentId());

        assertThat(ex.getMessage()).contains("No fue posible consultar el torneo activo");
    }

    @Test
    void getActiveTournamentId_deberiaLanzarExcepcionCuandoElServidorEstaCaido() {
        // Apagamos WireMock para este test — usamos un puerto distinto sin servidor
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9099")
                .build();
        TournamentClient clientSinServidor = new TournamentClientImpl(restClient, "/tournaments/active");

        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                clientSinServidor::getActiveTournamentId);
        assertThat(ex.getMessage()).contains("No fue posible consultar el torneo activo");
    }
}
