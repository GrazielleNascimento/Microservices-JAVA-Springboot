package br.com.petsgft.integration.catsapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CatApiPaths {

    @Value("${catapi.baseurl}")
    private String CATS_API_BASEURL;

    public String getTokenUrl() {
        return CATS_API_BASEURL + "/authenticate";
    }

    public String getFetchImageUrl(String breed) {
        return CATS_API_BASEURL + "/api/cats/fetch-images/" + breed;
    }
}
