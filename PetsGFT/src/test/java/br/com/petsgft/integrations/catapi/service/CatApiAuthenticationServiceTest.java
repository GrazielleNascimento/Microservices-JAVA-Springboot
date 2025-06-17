package br.com.petsgft.integrations.catapi.service;

import br.com.petsgft.integration.catsapi.model.CatApiAuthentication;
import br.com.petsgft.integration.catsapi.service.CatApiAuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CatApiAuthenticationServiceTest {

    @Autowired
    private CatApiAuthenticationService catApiAuthenticationService;

    @Autowired
    private CatApiAuthentication token;

    @Test
    public void shouldGetTokenWithSuccess() {

        // action
        catApiAuthenticationService.getToken();

        // assertations
        assertNotNull(token.getToken());
    }


}
