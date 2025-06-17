package br.com.mscloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
// Enable the service as a Eureka client
@EnableDiscoveryClient
public class MscloudgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MscloudgatewayApplication.class, args);
	}

	// Define the routes for the gateway service
	// to redirect the requests to the microservices
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/api/pets/**").uri("lb://mspets"))
				.route(r -> r.path("/appointment/**").uri("lb://mscarescheduling"))
				.route(r -> r.path("/api/notifications/**").uri("lb://msnotificationemail"))
				.build();
	}
}