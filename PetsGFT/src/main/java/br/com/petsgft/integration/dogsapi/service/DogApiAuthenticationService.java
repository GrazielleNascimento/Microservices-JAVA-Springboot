package br.com.petsgft.integration.dogsapi.service;

import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.integration.dogsapi.model.DogApiAuthentication;
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
public class DogApiAuthenticationService {

    @Autowired
    private DogApiPaths dogApiPaths;

    private final String DOGAPI_USER;
    private final String DOGAPI_PASSWORD;

    private final DogApiAuthentication token;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(DogApiAuthenticationService.class);

    @Autowired
    public DogApiAuthenticationService(
            DogApiAuthentication token,
            @Value("${dogapi.user}") String dogApiUser,
            @Value("${dogapi.password}") String dogApiPassword,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.token = token;
        this.DOGAPI_USER = dogApiUser;
        this.DOGAPI_PASSWORD = dogApiPassword;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        logger.info("DogApiAuthenticationService initialized with user: {} and password: {}", dogApiUser, dogApiPassword);
    }

    @PostConstruct
    public void initToken() {
//        logger.info("External Dog API needs to be running for the token to be generated.");
//        getToken();
    }

    public void getToken() {
        logger.info("Starting token generation process...");
        Map<String, String> body = new HashMap<>();
        body.put("username", DOGAPI_USER);
        body.put("password", DOGAPI_PASSWORD);

        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            logger.debug("Request body for token generation: {}", jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            logger.info("Sending authentication request to DogAPI...");

            ResponseEntity<String> response = restTemplate.exchange(
                    dogApiPaths.getTokenUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("Error fetching token from DogAPI: response status is not OK");
                throw new ExternalApiException("Error while trying to authenticate with DogAPI", null);
            }

            logger.info("Authenticated with DogAPI successfully. Token: {}", response.getBody());
            token.setToken(response.getBody());
            token.setCreatedAt(LocalDateTime.now());
            logger.debug("Token set with creation time: {}", token.getCreatedAt());

        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON for DogAPI authentication", e);
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