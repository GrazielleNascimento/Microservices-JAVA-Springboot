package com.cats.gft.controller;

import com.cats.gft.api.Response;
import com.cats.gft.catapi.CatResponseImageApiDto;
import com.cats.gft.dto.CatDto;
import com.cats.gft.exception.ExternalApiException;
import com.cats.gft.exception.ResourceNotFoundException;
import com.cats.gft.model.CatModel;
import com.cats.gft.service.CatService;
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

@RestController
@RequestMapping("/api/cats")
@Tag(name = "Cat", description = "Endpoints for managing cats")
@Validated
public class CatController {

    private final CatService catService;

    @Autowired
    public CatController(CatService catService) {
        this.catService = catService;
    }

    @PostMapping
    @Operation(summary = "Create a new cat")
    public ResponseEntity<CatModel> createCat(@RequestBody @Valid CatDto catDto) {
        try {
            CatModel createdCat = catService.createCat(catDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCat);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data", e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a cat by ID")
    public ResponseEntity<CatModel> getCatById(@PathVariable Long id) {

        CatModel cat = catService.getCatById(id);
        if (cat == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cat);
    }

    @GetMapping
    @Operation(summary = "Get all cats")
    public ResponseEntity<List<CatModel>> getAllCats() {
        List<CatModel> cats = catService.getAllCats();
        return ResponseEntity.ok(cats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a cat by ID")
    public ResponseEntity<Response<CatModel>> updateCat(@PathVariable Long id, @RequestBody @Valid CatDto catDto) {

        CatModel updatedCat = catService.updateCat(id, catDto);

        Response response = new Response();
        if (updatedCat == null) {
            response.setErrors("The cat with ID " + id + " was not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        response.setData(updatedCat);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a cat by ID")
    public ResponseEntity<String> deleteCat(@PathVariable Long id) {
        try {
            catService.deleteCat(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The cat with ID " + id + " was not found");
        }
    }


    @GetMapping("/breed/{breed}")
    @Operation(summary = "Get cats by breed")
    public ResponseEntity<Response<List<CatModel>>> getCatsByBreed(@PathVariable String breed) {
        List<CatModel> cats = catService.getCatsByBreed(breed);
        Response response = new Response();

        if (cats.isEmpty()) {
            response.setErrors("No cats found with breed: " + breed);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(cats);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get cats by name")
    public ResponseEntity<Response<List<CatModel>>> getCatsByName(@PathVariable String name) {
        List<CatModel> cats = catService.getCatsByName(name);

        Response response = new Response();

        if (cats.isEmpty()) {
            response.setErrors("No cats found with name: " + name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(cats);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/breed-and-name")
    @Operation(summary = "Get cats by breed and name")
    public ResponseEntity<Response<List<CatModel>>> getCatsByBreedAndName(@RequestParam String breed, @RequestParam String name) {
        List<CatModel> cats = catService.getCatsByBreedAndName(breed, name);

        Response response = new Response();

        if(cats.isEmpty()) {
            response.setErrors("No cats found with breed: " + breed + " and name: " + name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(cats);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-image")
    @Operation(summary = "Get cats with image URL")
    public ResponseEntity<Response<List<CatModel>>> getCatsWithImageUrl() {
        List<CatModel> cats = catService.getCatsWithImageUrl();

        Response response = new Response();

        if(cats.isEmpty()) {
            response.setErrors("No cats found with image URL");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(cats);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name-contains/{keyword}")
    @Operation(summary = "Get cats whose name contains a keyword")
    public ResponseEntity<Response<List<CatModel>>> getCatsByNameContaining(@PathVariable String keyword) {
        List<CatModel> cats = catService.getCatsByNameContaining(keyword);

        Response response = new Response();

        if(cats.isEmpty()) {
            response.setErrors("No cats found with name containing: " + keyword);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(cats);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/breed-contains/{keyword}")
    @Operation(summary = "Get cats whose breed contains a keyword")
    public ResponseEntity<Response<List<CatModel>>> getCatsByBreedContaining(@PathVariable String keyword) {
        List<CatModel> cats = catService.getCatsByBreedContaining(keyword);

        Response response = new Response();

        if(cats.isEmpty()) {
            response.setErrors("No cats found with breed containing: " + keyword);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(cats);

        return ResponseEntity.ok(response);
    }

    // Endpoint for external API: Fetch all cat breeds
    @GetMapping("/fetch-all-breeds")
    @Operation(summary = "Fetch all cat breeds from The Cat API")
    public ResponseEntity<String> fetchAllBreedsFromExternal() {
        try {
            String breeds = catService.fetchAllBreedsFromExternal();
            return ResponseEntity.ok(breeds);
        } catch (ExternalApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching breeds from external API", e);
        }
    }

    // Endpoint for external API: Fetch images of cats by breed
    @GetMapping("/fetch-images/{breed}")
    @Operation(summary = "Fetch images of cats/cats by breed from The Cat API")
    public ResponseEntity<Response<CatResponseImageApiDto>> fetchImagesByBreedId(@PathVariable("breed") String breed) {
        try {
            CatResponseImageApiDto catResponseImageApiDto = catService.fetchImagesByBreedId(breed);

            Response response = new Response();

            if (catResponseImageApiDto == null) {
                response.setErrors("Image not found for breed: " + breed);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.setData(catResponseImageApiDto);

            return ResponseEntity.ok(response);

        } catch (ExternalApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching images from external API", e);
        }
    }

    // Endpoint for external API: Fetch cat breeds and optionally store in local database
    @GetMapping("/fetch-breeds")
    @Operation(summary = "Fetch cat breeds from The Cat API and optionally store in local database")
    public ResponseEntity<String> fetchCatBreeds(@RequestParam boolean store) {
        try {
            String response = catService.fetchAndStoreBreeds(store);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching cat breeds from external API", e);
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
