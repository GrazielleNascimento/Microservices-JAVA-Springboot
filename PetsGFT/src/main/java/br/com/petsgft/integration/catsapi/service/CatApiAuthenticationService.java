package br.com.petsgft.integration.catsapi.service;

import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.integration.catsapi.model.CatApiAuthentication;
import br.com.petsgft.integration.dogsapi.service.DogApiPaths;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CatApiAuthenticationService {

    private final String CATAPI_USER;
    private final String CATAPI_PASSWORD;

    private final CatApiAuthentication token;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(CatApiAuthenticationService.class);

    @Autowired
    private CatApiPaths catApiPaths;

    @Autowired
    public CatApiAuthenticationService(
            CatApiAuthentication token,
            @Value("${catapi.user}") String catApiUser,
            @Value("${catapi.password}") String catApiPassword,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.token = token;
        this.CATAPI_USER = catApiUser;
        this.CATAPI_PASSWORD = catApiPassword;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        logger.info("CatApiAuthenticationService initialized with user: {} and password: {}", catApiUser, catApiPassword);
    }

    @PostConstruct
    private void initToken() {
//        logger.info("External Cat API needs to be running for the token to be generated.");
      //  getToken();
    }

    public void getToken() {
        logger.info("Starting token generation process...");
        Map<String, String> body = new HashMap<>();
        body.put("username", CATAPI_USER);
        body.put("password", CATAPI_PASSWORD);

        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            logger.debug("Request body for token generation: {}", jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            logger.info("Sending authentication request to CatAPI...");



            ResponseEntity<String> response = restTemplate.exchange(
                    catApiPaths.getTokenUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response == null || response.getBody() == null) {
                logger.error("Error fetching token from CatAPI: response is null or empty");
                throw new ExternalApiException("Error fetching token from CatAPI", null);
            }

            logger.info("Authenticated with CatAPI successfully. Token: {}", response.getBody());
            token.setToken(response.getBody());
            token.setCreatedAt(LocalDateTime.now());
            logger.debug("Token set with creation time: {}", token.getCreatedAt());

        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON for CatAPI authentication", e);
            throw new RuntimeException("Error parsing JSON", e);
        }
    }



    public void validateToken() {
        logger.info("Validating token...");
        if (token.getToken() == null
                || token.getCreatedAt() == null
                || token.getCreatedAt().plusHours(2).isBefore(LocalDateTime.now())) {
            logger.info("Token is invalid or expired, fetching a new token...");
            getToken();
        } else {
            logger.info("Token is valid.");
        }
    }
}