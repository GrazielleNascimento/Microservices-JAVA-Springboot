package com.dogs.gft.service;

import com.dogs.gft.api.dog.DogResponseApiDto;
import com.dogs.gft.api.dog.DogResponseImageApiDto;
import com.dogs.gft.dto.DogDto;
import com.dogs.gft.exception.ExternalApiException;
import com.dogs.gft.exception.ResourceNotFoundException;
import com.dogs.gft.model.DogModel;
import com.dogs.gft.repository.DogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DogService {

    private static final Logger logger = LoggerFactory.getLogger(DogService.class);
    private final ObjectMapper objectMapper;

    @Value("${thedogapi.apikey}")
    private String apiKey;

    @Autowired
    RestTemplate restTemplate = new RestTemplate();

    private final String apiUrl = "https://api.thedogapi.com/v1";
    private final DogRepository dogRepository;

    public DogService(DogRepository dogRepository, ObjectMapper objectMapper) {
        this.dogRepository = dogRepository;
        this.objectMapper = objectMapper;
    }

    public DogModel createDog(@Valid DogDto dogDto) {
        logger.info("Creating a new dog with name: {}", dogDto.getName());

        Map<String, Object> imageMap = fetchImagesByBreed(dogDto.getImageUrl(), 1);

        DogModel dog = new DogModel();
        dog.setName(dogDto.getName());
        dog.setBreed(dogDto.getBreed());
        dog.setImageUrl(imageMap.get("imageUrl").toString());
        DogModel createdDog = dogRepository.save(dog);
        logger.info("Dog with ID: {} created successfully", createdDog.getId());
        return createdDog;
    }

    public DogModel getDogById(Long id) {
        logger.info("Fetching dog with ID: {}", id);
        return dogRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Dog not found with ID: {}", id);
                    return new ResourceNotFoundException("Dog not found with ID: " + id);
                });
    }

    public List<DogModel> getAllDogs() {
        logger.info("Fetching all dogs");
        return dogRepository.findAll();
    }

    public DogModel updateDog(Long id, @Valid DogDto dogDto) {
        logger.info("Updating dog with ID: {}", id);

        DogModel existingDog = getDogById(id);
        existingDog.setName(dogDto.getName());
        existingDog.setBreed(dogDto.getBreed());

        if (dogDto.getImageUrl() != null) {
            existingDog.setImageUrl(dogDto.getImageUrl());
        }

        DogModel updatedDog = dogRepository.save(existingDog);
        logger.info("Dog with ID: {} updated successfully", updatedDog.getId());
        return updatedDog;
    }

    public void deleteDog(Long id) {
        logger.info("Deleting dog with ID: {}", id);
        DogModel existingDog = getDogById(id);
        dogRepository.delete(existingDog);
        logger.info("Dog with ID: {} deleted successfully", id);
    }

    public List<DogModel> getDogsByBreed(String breed) {
        logger.info("Fetching dogs with breed: {}", breed);
        return dogRepository.findByBreed(breed);
    }

    public List<DogModel> getDogsByName(String name) {
        logger.info("Fetching dogs with name: {}", name);
        return dogRepository.findByName(name);
    }

    public List<DogModel> getDogsByBreedAndName(String breed, String name) {
        logger.info("Fetching dogs with breed: {} and name: {}", breed, name);
        return dogRepository.findByBreedAndName(breed, name);
    }

    public List<DogModel> getDogsWithImageUrl() {
        logger.info("Fetching dogs with image URL");
        return dogRepository.findByImageUrlIsNotNull();
    }

    public List<DogModel> getDogsByNameContaining(String keyword) {
        logger.info("Fetching dogs with name containing: {}", keyword);
        return dogRepository.findByNameContaining(keyword);
    }

    public List<DogModel> getDogsByBreedContaining(String keyword) {
        logger.info("Fetching dogs with breed containing: {}", keyword);
        return dogRepository.findByBreedContaining(keyword);
    }


    //Method to list all breeds and IDs from the external API
    public String fetchAllBreedsFromExternal() {
        String url = apiUrl + "/breeds";
        try {
            logger.info("Fetching all breeds from external API");
            return restTemplate.getForObject(url + "?api_key=" + apiKey, String.class);
        } catch (Exception e) {
            logger.error("Error fetching breeds from external API", e);
            throw new ExternalApiException("Error fetching breeds from external API");
        }
    }


    public Map<String, Object> fetchImagesByBreed(String breed, int limit) {
        String url = apiUrl + "/images/search";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("limit", limit)
                .queryParam("name", breed)
                .queryParam("api_key", apiKey);

        String fullUrl = builder.toUriString();

        try {
            logger.info("Fetching images for breed: {} from The Dog API...", breed);
            ResponseEntity<List<DogResponseImageApiDto>> dogResponse = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DogResponseImageApiDto>>() {
                    }
            );


            List<DogResponseImageApiDto> dogResponses = dogResponse.getBody();
            if (dogResponses == null || dogResponses.isEmpty()) {
                throw new ExternalApiException("No images found for breed: " + breed);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("breed", breed);
            map.put("imageUrl", dogResponses.get(0).getUrl());

            return map;

        } catch (Exception e) {
            logger.error("Error fetching images from The Dog API", e);
            throw new ExternalApiException("Error fetching images from The Dog API");
        }
    }


    public String fetchAndStoreBreeds(boolean store) {
        String url = apiUrl + "/breeds";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("api_key", apiKey);
        String fullUrl = builder.toUriString();

        try {
            logger.info("Fetching breeds from The Dog API...");
            ResponseEntity<List<DogResponseApiDto>> dogResponse = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }

            );

            List<DogResponseApiDto> dogResponses = dogResponse.getBody();
            if (dogResponses == null || dogResponses.isEmpty()) {
                throw new ExternalApiException("No breeds found from The Dog API");
            }


            if (store) {
                List<DogModel> dogModels = new ArrayList<>();

                for (DogResponseApiDto dogResponseApiDto : dogResponses) {
                    DogModel dogModel = new DogModel();
                    String name = dogResponseApiDto.getBredFor();
                    if (name == null || name.isBlank()) {
                        logger.warn("Skipping dog with missing or blank name");
                        continue;
                    }
                    String breed = dogResponseApiDto.getName();
                    if (breed == null || breed.isBlank()) {
                        logger.warn("Skipping dog with missing or blank breed");
                        continue;
                    }
                    dogModel.setName(name);
                    dogModel.setBreed(breed);
                    dogModel.setImageUrl(dogResponseApiDto.getImage().getUrl());
                    dogModels.add(dogModel);
                }

                if (!dogModels.isEmpty()) {
                    dogRepository.saveAll(dogModels);
                    logger.info("BreedsDto stored in the database successfully.");
                } else {
                    logger.warn("No valid dog models to store in the database.");
                }
            }

            return objectMapper.writeValueAsString(dogResponses);
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.error("Optimistic locking failure", e);
            throw new ExternalApiException("Optimistic locking failure");
        } catch (Exception e) {
            logger.error("Error fetching breeds from The Dog API", e);
            throw new ExternalApiException("Error fetching breeds from The Dog API");
        }
    }


}


