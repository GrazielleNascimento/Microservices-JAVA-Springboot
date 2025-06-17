package br.com.petsgft.integration.dogsapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class DogApiPaths {

    @Value("${dogapi.baseurl}")
    private String DOG_API_BASE_URL;

    public String getTokenUrl() {
        return DOG_API_BASE_URL + "/authenticate";
    }

    public String getFetchImageUrl(String breed, int limit) {
        return DOG_API_BASE_URL + "/api/dogs/fetch-images/" + breed + "/" + limit;
    }

}
