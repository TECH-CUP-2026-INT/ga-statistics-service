package co.edu.escuelaing.techcup.statistics.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI statisticsServiceOpenApi() {
        var securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Autenticación mediante token JWT. " +
                        "Incluir en el header Authorization: Bearer {token}");
        return new OpenAPI()
                .info(new Info()
                        .title("TechCup Statistics Service API")
                        .description("Microservicio de estadísticas para TechCup Fútbol. " +
                                "Calcula y expone estadísticas de jugadores, equipos, partidos y torneos.")
                        .version("v1.0")
                        .contact(new Contact().name("TechCup Statistics Team")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
