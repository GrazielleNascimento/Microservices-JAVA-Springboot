package br.com.petsgft.integration.catsapi.service;

import br.com.petsgft.api.Response;
import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.integration.catsapi.dto.CatApiImageDTO;
import br.com.petsgft.integration.catsapi.model.CatApiAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CatApiService {

    private static final Logger logger = LoggerFactory.getLogger(CatApiService.class);

    private final RestTemplate restTemplate;
    private final CatApiAuthentication token;
    private final CatApiAuthenticationService catApiAuthenticationService;

    @Value("${catapi.baseurl}")
    private String catApiBaseUrl;

    @Autowired
    public CatApiService(
            RestTemplate restTemplate,
            CatApiAuthentication token,
            CatApiAuthenticationService catApiAuthenticationService
    ) {
        this.restTemplate = restTemplate;
        this.token = token;
        this.catApiAuthenticationService = catApiAuthenticationService;
    }

    public CatApiImageDTO fetchImage(String breed) {

        String path = catApiBaseUrl + "/api/cats/fetch-images/" + breed;
        logger.info("Fetching image to " + breed + " from API on url " + path);

        try {
            HttpHeaders headers = new HttpHeaders();
            catApiAuthenticationService.validateToken();
            headers.setBearerAuth(token.getToken());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Response<CatApiImageDTO>> response = restTemplate.exchange(
                    path,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Response<CatApiImageDTO>>() {}
            );

            if (response == null || (response.getBody().getData() == null && !response.getBody().getErrors().isEmpty())) {
                throw new ExternalApiException(response.getBody().getErrors(), null);
            }

            CatApiImageDTO catApiImageDTO = response.getBody().getData();
            return catApiImageDTO;

        } catch (Exception e) {
            logger.error("Error fetching image from API", e);
            throw new ExternalApiException("Error fetching image from API", e);
        }
    }

}
