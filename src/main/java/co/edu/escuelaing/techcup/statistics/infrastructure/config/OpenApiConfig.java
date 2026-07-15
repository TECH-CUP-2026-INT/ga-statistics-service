package co.edu.escuelaing.techcup.statistics.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI statisticsServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechCup Statistics Service API")
                        .description("Microservicio de estadísticas para TechCup Fútbol. " +
                                "Calcula y expone estadísticas de jugadores, equipos, partidos y torneos.")
                        .version("v1.0")
                        .contact(new Contact().name("TechCup Statistics Team")));
    }
}
