package br.com.petsgft.integrations.dogapi.service;

import br.com.petsgft.integration.dogsapi.model.DogApiAuthentication;
import br.com.petsgft.integration.dogsapi.service.DogApiAuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DogApiAuthenticationServiceTest {

    @Autowired
    DogApiAuthenticationService dogApiAuthenticationService;

    @Autowired
    DogApiAuthentication token;

    @Test
    public void shouldGetTokenWithSuccess() {
        // action
        dogApiAuthenticationService.getToken();

        // assertion
        assertNotNull(token.getToken());
        assertNotNull(token.getCreatedAt());
    }

}
