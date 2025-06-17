package br.com.petsgft.integration.dogsapi.service;

import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.integration.dogsapi.dto.DogApiImageDTO;
import br.com.petsgft.integration.dogsapi.model.DogApiAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;


@Service
public class DogApiService {

    @Autowired
    private DogApiPaths dogApiPaths;

    private final DogApiAuthenticationService dogApiAuthenticationService;
    private final DogApiAuthentication token;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DogApiService.class);

    @Autowired
    public DogApiService(
            RestTemplate restTemplate,
            DogApiAuthenticationService dogApiAuthenticationService,
            DogApiAuthentication token
    ) {
        this.restTemplate = restTemplate;
        this.dogApiAuthenticationService = dogApiAuthenticationService;
        this.token = token;
    }

    public DogApiImageDTO fetchImage(String breed) {

        String path = dogApiPaths.getFetchImageUrl(breed, 1);
        logger.info("Fetching image to " + breed + " from API on url " + path);

        try {
            HttpHeaders headers = new HttpHeaders();
            dogApiAuthenticationService.validateToken();
            headers.setBearerAuth(token.getToken());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<DogApiImageDTO> response = restTemplate.exchange(
                    path,
                    HttpMethod.GET,
                    entity,
                    DogApiImageDTO.class
            );

            if (response == null || response.getBody() == null) {
                throw new ExternalApiException("Error fetching image from API", null);
            }

            DogApiImageDTO dogImage = response.getBody();
            return  dogImage;

        } catch (Exception e) {
            logger.error("Error fetching image from API", e);
            throw new ExternalApiException("Error fetching image from API", e);
        }
    }

}
