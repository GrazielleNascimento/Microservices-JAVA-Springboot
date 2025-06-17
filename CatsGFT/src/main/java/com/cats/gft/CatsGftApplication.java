package com.cats.gft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@EnableConfigurationProperties
public class CatsGftApplication {

    private static final Logger LOG = LoggerFactory.getLogger(CatsGftApplication.class);

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(CatsGftApplication.class, args);
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
        };
    }
}