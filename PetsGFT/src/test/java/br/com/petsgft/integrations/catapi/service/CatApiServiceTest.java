package br.com.petsgft.integrations.catapi.service;

import br.com.petsgft.integration.catsapi.dto.CatApiImageDTO;
import br.com.petsgft.integration.catsapi.service.CatApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CatApiServiceTest {

    @Autowired
    private CatApiService catApiService;


    @Test
    public void shouldFetchImageWithSuccess() {
        // preparation
        String breed = "bengal";

        // action
        CatApiImageDTO catApiImageDTO = catApiService.fetchImage(breed);

        // assertaations
        assertNotNull(catApiImageDTO);
        assertNotNull(catApiImageDTO.getUrl());
    }

}
