package co.edu.escuelaing.techcup.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceStatisticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceStatisticsApplication.class, args);
	}

}
