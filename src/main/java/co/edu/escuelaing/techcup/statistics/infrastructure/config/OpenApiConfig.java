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
                        .description("""
                                Statistics microservice for TechCup Futbol. Computes and exposes \
                                player, team, match, and tournament statistics derived from the \
                                match events reported by the Competition (arbitraje en vivo) \
                                service.""")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("TechCup Statistics Team")));
    }
}
