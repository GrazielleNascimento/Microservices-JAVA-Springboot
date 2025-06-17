package br.com.petsgft.core.config;

import br.com.petsgft.integration.catsapi.model.CatApiAuthentication;
import br.com.petsgft.integration.dogsapi.model.DogApiAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DogApiAuthentication dogApiAuthentication() {
        return new DogApiAuthentication();
    }

    @Bean
    public CatApiAuthentication catApiAuthnetication() {
        return new CatApiAuthentication();
    }

}