package br.com.petsgft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
public class PetsGftApplication {

	private static final Logger LOG = LoggerFactory.getLogger(PetsGftApplication.class);

	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(PetsGftApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			LOG.info("Application started successfully");
			LOG.info("Java version: " + System.getProperty("java.version"));

			RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
			handlerMapping.getHandlerMethods().forEach((key, value) -> {
				LOG.info("Route: " + key + " -> Method " + value);
			});

			String serverPort = context.getEnvironment().getProperty("local.server.port");

			LOG.info("Pet service is running on port: " + serverPort);
			LOG.info("Swagger UI is available at: http://localhost:" + serverPort + "/swagger-ui/index.html#/");

			String rabbitmqHost = context.getEnvironment().getProperty("spring.rabbitmq.host");
			String rabbitmqPort = context.getEnvironment().getProperty("spring.rabbitmq.port");
			LOG.info("RabbitMQ is connecting on host: " + rabbitmqHost + " and port: " + rabbitmqPort);

			String mysqlUrl = context.getEnvironment().getProperty("spring.datasource.url");
			String mysqlUsername = context.getEnvironment().getProperty("spring.datasource.username");
			LOG.info("MySQL is connecting to: " + mysqlUrl + " with username: " + mysqlUsername);
		};
	}
}