package br.com.msnotificationemail;

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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableScheduling
public class MsNotificationEmailApplication {

	private static final Logger LOG = LoggerFactory.getLogger(MsNotificationEmailApplication.class);

	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(MsNotificationEmailApplication.class, args);
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
			if (serverPort == null) {
				serverPort = context.getEnvironment().getProperty("server.port");
			}

			LOG.info("Notification service is running on port: " + serverPort);


			String rabbitmqHost = context.getEnvironment().getProperty("spring.rabbitmq.host");
			String rabbitmqPort = context.getEnvironment().getProperty("spring.rabbitmq.port");
			LOG.info("RabbitMQ is connecting on host: " + rabbitmqHost + " and port: " + rabbitmqPort);

		};
	}
}