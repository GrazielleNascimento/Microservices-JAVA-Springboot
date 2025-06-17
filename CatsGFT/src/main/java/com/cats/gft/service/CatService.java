package com.cats.gft.service;

import com.cats.gft.catapi.CatResponseApiDto;
import com.cats.gft.catapi.CatResponseImageApiDto;
import com.cats.gft.dto.CatDto;
import com.cats.gft.exception.ExternalApiException;
import com.cats.gft.exception.ResourceNotFoundException;
import com.cats.gft.model.CatModel;
import com.cats.gft.repository.CatRepository;
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

import java.util.*;

@Service
public class CatService {

    private static final Logger logger = LoggerFactory.getLogger(CatService.class);
    private final ObjectMapper objectMapper;

    @Value("${thecatapi.apikey}")
    private String apiKey;

    @Autowired
    RestTemplate restTemplate = new RestTemplate();

    private final String apiUrl = "https://api.thecatapi.com/v1";
    private final CatRepository catRepository;

    @Autowired
    public CatService(CatRepository catRepository, ObjectMapper objectMapper) {
        this.catRepository = catRepository;
        this.objectMapper = objectMapper;
    }

    public CatModel createCat(@Valid CatDto catDto) {
        logger.info("Creating a new cat with name: {}", catDto.getName());

        CatModel cat = new CatModel();
        cat.setName(catDto.getName());
        cat.setBreed(catDto.getBreed());

        CatResponseImageApiDto imageCat = fetchImagesByBreedId(catDto.getBreed());
        cat.setImageUrl(imageCat.getUrl());

        CatModel createdCat = catRepository.save(cat);
        logger.info("cat with ID: {} created successfully", createdCat.getId());
        return createdCat;
    }

    public CatModel getCatById(Long id) {
        logger.info("Fetching cat with ID: {}", id);

        Optional<CatModel> opCatModel = catRepository.findById(id);
        if (!opCatModel.isPresent()) {
            return null;
        }

        return opCatModel.get();
    }

    public List<CatModel> getAllCats() {
        logger.info("Fetching all cats");
        return catRepository.findAll();
    }

    public CatModel updateCat(Long id, @Valid CatDto catDto) {
        logger.info("Updating cat with ID: {}", id);

        CatModel existingCat = getCatById(id);
        if (existingCat == null) {
            return null;
        }

        existingCat.setName(catDto.getName());
        existingCat.setBreed(catDto.getBreed());

        if (catDto.getImageUrl() != null) {
            existingCat.setImageUrl(catDto.getImageUrl());
        }

        CatModel updatedCat = catRepository.save(existingCat);
        logger.info("Cat with ID: {} updated successfully", updatedCat.getId());
        return updatedCat;
    }

    public void deleteCat(Long id) {
        logger.info("Deleting cat with ID: {}", id);

        CatModel existingCat = getCatById(id);
        if (existingCat == null) {
            throw new ResourceNotFoundException("Cat not found");
        }

        catRepository.delete(existingCat);
        logger.info("Cat with ID: {} deleted successfully", id);
    }

    public List<CatModel> getCatsByBreed(String breed) {
        logger.info("Fetching cats with breed: {}", breed);
        return catRepository.findByBreed(breed);
    }

    public List<CatModel> getCatsByName(String name) {
        logger.info("Fetching cats with name: {}", name);
        return catRepository.findByName(name);
    }

    public List<CatModel> getCatsByBreedAndName(String breed, String name) {
        logger.info("Fetching cats with breed: {} and name: {}", breed, name);
        return catRepository.findByBreedAndName(breed, name);
    }

    public List<CatModel> getCatsWithImageUrl() {
        logger.info("Fetching cats with image URL");
        return catRepository.findByImageUrlIsNotNull();
    }

    public List<CatModel> getCatsByNameContaining(String keyword) {
        logger.info("Fetching cats with name containing: {}", keyword);
        return catRepository.findByNameContaining(keyword);
    }

    public List<CatModel> getCatsByBreedContaining(String keyword) {
        logger.info("Fetching cats with breed containing: {}", keyword);
        return catRepository.findByBreedContaining(keyword);
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

    public CatResponseApiDto fetchCatByBreed(String breedName) {
        // https://api.thecatapi.com/v1/breeds/search?q=American Shorthair&attach_image=1
        String url = apiUrl + "/breeds/search";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", breedName)
                .queryParam("attach_image", 1)
                .queryParam("api_key", apiKey);

        String fullUrl = builder.toUriString();

        try {
            ResponseEntity<List<CatResponseApiDto>> catResponse = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CatResponseApiDto>>() {
                    }
            );

            CatResponseApiDto catResponses = catResponse.getBody().get(0);
            if (catResponses == null) {
                throw new ExternalApiException("No image found for breedId: " + breedName);
            }

            return catResponses;

        } catch (Exception e) {
            logger.error("Error fetching images from The Cat API", e);
            throw new ExternalApiException("Error fetching images from The Cat API");
        }
    }

    //Method to fetch images of cats by breed from the external API
    public CatResponseImageApiDto fetchImagesByBreedId(String breedId) {
        String url = apiUrl + "/images/search";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("name", breedId)
                .queryParam("api_key", apiKey);

        String fullUrl = builder.toUriString();

        try {
            logger.info("Fetching images for breedId: {} from The Cat API...", breedId);
            ResponseEntity<List<CatResponseImageApiDto>> catResponse = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CatResponseImageApiDto>>() {
                    }
            );

            CatResponseImageApiDto catResponses = catResponse.getBody().get(0);
            if (catResponses == null) {
                throw new ExternalApiException("No image found for breedId: " + breedId);
            }

            return catResponses;

        } catch (Exception e) {
            logger.error("Error fetching images from The Cat API", e);
            throw new ExternalApiException("Error fetching images from The Cat API");
        }
    }


    public String fetchAndStoreBreeds(boolean store) {
        String url = apiUrl + "/breeds";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("api_key", apiKey);
        String fullUrl = builder.toUriString();

        try {
            logger.info("Fetching breeds from The Cat API...");
            ResponseEntity<List<CatResponseApiDto>> catResponse = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }

            );

            List<CatResponseApiDto> catResponses = catResponse.getBody();
            if (catResponses == null || catResponses.isEmpty()) {
                throw new ExternalApiException("No breeds found from The Cat API");
            }

            if (store) {
                List<CatModel> catModels = new ArrayList<>();

                for (CatResponseApiDto catResponseApiDto : catResponses) {
                    CatModel catModel = new CatModel();

                    catModel.setName(catResponseApiDto.getName());
                    catModel.setBreed(catResponseApiDto.getName());
                    CatResponseImageApiDto catResponseImageApiDto = fetchImagesByBreedId(catResponseApiDto.getReferenceImageId());
                    catModel.setImageUrl(catResponseImageApiDto.getUrl());

                    catModels.add(catModel);
                }

                if (catModels.isEmpty()) {
                    logger.warn("No valid cat models to store in the database.");
                    throw new RuntimeException("No valid cat models to store in the database.");
                }

                catRepository.saveAll(catModels);
                logger.info("BreedsDto stored in the database successfully.");
            }

            return objectMapper.writeValueAsString(catResponses);
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.error("Optimistic locking failure", e);
            throw new ExternalApiException("Optimistic locking failure");
        } catch (Exception e) {
            logger.error("Error fetching breeds from The Cat API", e);
            throw new ExternalApiException("Error fetching breeds from The Cat API");
        }
    }


}


