package com.dogs.gft.controller;

import com.dogs.gft.dto.DogDto;
import com.dogs.gft.dto.DogDtoRecord;
import com.dogs.gft.exception.ExternalApiException;
import com.dogs.gft.exception.ResourceNotFoundException;
import com.dogs.gft.model.DogModel;
import com.dogs.gft.service.DogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dogs")
@Tag(name = "Dog", description = "Endpoints for managing dogs")
@Validated
public class DogController {

    private final DogService dogService;

    @Autowired
    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @PostMapping
    @Operation(summary = "Create a new dog")
    public ResponseEntity<DogModel> createDog(@RequestBody @Valid DogDto dogDto) {
        try {
            DogModel createdDog = dogService.createDog(dogDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDog);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data", e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a dog by ID")
    public ResponseEntity<DogModel> getDogById(@PathVariable Long id) {
        try {
            DogModel dog = dogService.getDogById(id);
            return ResponseEntity.ok(dog);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dog not found", e);
        }
    }

    @GetMapping
    @Operation(summary = "Get all dogs")
    public ResponseEntity<List<DogModel>> getAllDogs() {
        List<DogModel> dogs = dogService.getAllDogs();
        return ResponseEntity.ok(dogs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dog by ID")
    public ResponseEntity<DogModel> updateDog(@PathVariable Long id, @RequestBody @Valid DogDto dogDto) {
        try {
            DogModel updatedDog = dogService.updateDog(id, dogDto);
            return ResponseEntity.ok(updatedDog);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dog not found", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data", e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dog by ID")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        try {
            dogService.deleteDog(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dog not found", e);
        }
    }

    @GetMapping("/breed/{breed}")
    @Operation(summary = "Get dogs by breed")
    public ResponseEntity<List<DogModel>> getDogsByBreed(@PathVariable String breed) {
        List<DogModel> dogs = dogService.getDogsByBreed(breed);
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get dogs by name")
    public ResponseEntity<List<DogModel>> getDogsByName(@PathVariable String name) {
        List<DogModel> dogs = dogService.getDogsByName(name);
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/breed-and-name")
    @Operation(summary = "Get dogs by breed and name")
    public ResponseEntity<List<DogModel>> getDogsByBreedAndName(@RequestParam String breed, @RequestParam String name) {
        List<DogModel> dogs = dogService.getDogsByBreedAndName(breed, name);
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/with-image")
    @Operation(summary = "Get dogs with image URL")
    public ResponseEntity<List<DogModel>> getDogsWithImageUrl() {
        List<DogModel> dogs = dogService.getDogsWithImageUrl();
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/name-contains/{keyword}")
    @Operation(summary = "Get dogs whose name contains a keyword")
    public ResponseEntity<List<DogModel>> getDogsByNameContaining(@PathVariable String keyword) {
        List<DogModel> dogs = dogService.getDogsByNameContaining(keyword);
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/breed-contains/{keyword}")
    @Operation(summary = "Get dogs whose breed contains a keyword")
    public ResponseEntity<List<DogModel>> getDogsByBreedContaining(@PathVariable String keyword) {
        List<DogModel> dogs = dogService.getDogsByBreedContaining(keyword);
        return ResponseEntity.ok(dogs);
    }

    // Endpoint for external API: Fetch all dog breeds
    @GetMapping("/fetch-all-breeds")
    @Operation(summary = "Fetch all dog breeds from The Dog API")
    public ResponseEntity<String> fetchAllBreedsFromExternal() {
        try {
            String breeds = dogService.fetchAllBreedsFromExternal();
            return ResponseEntity.ok(breeds);
        } catch (ExternalApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching breeds from external API", e);
        }
    }

    // Endpoint for external API: Fetch images of dogs by breed
    @GetMapping("/fetch-images/{breed}/{limit}")
    @Operation(summary = "Fetch images of dogs by breed from The Dog API")
    public ResponseEntity<String> fetchImagesByBreed(@PathVariable("breed") String breed, @PathVariable("limit") int limit) {
        try {
            Map<String, Object> mapImage = dogService.fetchImagesByBreed(breed, limit);

            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writeValueAsString(mapImage);

            return ResponseEntity.ok(response);

        } catch (ExternalApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching images from external API", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Endpoint for external API: Fetch dog breeds and optionally store in local database
    @GetMapping("/fetch-breeds")
    @Operation(summary = "Fetch dog breeds from The Dog API and optionally store in local database")
    public ResponseEntity<String> fetchDogBreeds(@RequestParam boolean store) {
        try {
            String response = dogService.fetchAndStoreBreeds(store);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching dog breeds from external API", e);
        }
    }



    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleResourceNotFoundException() {
    }

    @ExceptionHandler(ExternalApiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public void handleExternalApiException() {
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleGeneralException() {
    }
}
