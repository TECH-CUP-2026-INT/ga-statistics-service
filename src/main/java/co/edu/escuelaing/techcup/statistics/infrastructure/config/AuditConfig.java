package co.edu.escuelaing.techcup.statistics.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class AuditConfig {

    private static final String SYSTEM_ACTOR = "statistics-service";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(SYSTEM_ACTOR);
    }
}
