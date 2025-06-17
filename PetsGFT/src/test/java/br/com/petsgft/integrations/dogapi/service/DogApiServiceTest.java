package br.com.petsgft.integrations.dogapi.service;

import br.com.petsgft.integration.dogsapi.dto.DogApiImageDTO;
import br.com.petsgft.integration.dogsapi.service.DogApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DogApiServiceTest {

    @Autowired
    DogApiService dogApiService;

    @Test
    public void setDogApiService() {
        // preparation
        String breed = "Bulldog";

        // action
        DogApiImageDTO bulldogImage = dogApiService.fetchImage(breed);

        // assertion
        assertNotNull(bulldogImage);
        assertNotNull(bulldogImage.getImageUrl());

    }

}
