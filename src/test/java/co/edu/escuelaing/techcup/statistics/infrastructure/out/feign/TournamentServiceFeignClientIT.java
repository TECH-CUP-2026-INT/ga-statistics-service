package co.edu.escuelaing.techcup.statistics.infrastructure.out.feign;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integración con Feign + WireMock.
 * Activa el perfil {@code feign} para que Spring inyecte
 * {@link TournamentFeignAdapter} en lugar de {@link TournamentClientImpl}.
 * <p>
 * Requiere que WireMock esté corriendo en el puerto 9092
 * y que la propiedad services.tournaments.base-url apunte allí.
 */
@SpringBootTest(properties = {
    "services.tournaments.base-url=http://localhost:9092",
    "spring.data.mongodb.uri=mongodb://localhost:27017/test"
})
@ActiveProfiles("feign")
@WireMockTest(httpPort = 9092)
class TournamentServiceFeignClientIT {

    @Autowired
    private TournamentClient tournamentClient;

    @Test
    void getActiveTournamentId_conFeign_deberiaRetornarId() {
        stubFor(get("/tournaments/active")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"feign-tournament-001\"}")));

        String result = tournamentClient.getActiveTournamentId();
        assertThat(result).isEqualTo("feign-tournament-001");
    }
}
