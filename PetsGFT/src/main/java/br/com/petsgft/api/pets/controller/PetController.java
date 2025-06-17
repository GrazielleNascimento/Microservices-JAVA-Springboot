package br.com.petsgft.api.pets.controller;

import br.com.petsgft.api.Response;
import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.model.PetSpeciesEnum;
import br.com.petsgft.api.pets.service.PetService;
import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.api.exception.FilterException;
import br.com.petsgft.api.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pet Management System", description = "Operations pertaining to pet management")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/status")
    public String status() {
        return "Pet service is running!";
    }

    @PostMapping
    @Operation(summary = "Create a new pet", description = "Create a new pet with the provided details", tags = {"Pet Management System"})
    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody PetDTO petDTO) {
        PetDTO createdPet = petService.createPet(petDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet by ID", description = "Retrieve a pet by its ID", tags = {"Pet Management System"})
    public ResponseEntity<Response<PetDTO>> getPetById(@PathVariable Integer id) {
        PetDTO pet = petService.getPetById(id);

        Response response = new Response<PetDTO>();

        if (pet == null) {
            response.setErrors("Pet not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(pet);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pet", description = "Update a pet's details by its ID", tags = {"Pet Management System"})
    public ResponseEntity<Response<PetDTO>> updatePet(@PathVariable Integer id, @Valid @RequestBody PetDTO petDTO) {
        PetDTO updatedPet = petService.updatePet(id, petDTO);

        Response response = new Response<PetDTO>();

        if (updatedPet == null) {
            response.setErrors("Pet not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(updatedPet);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pet", description = "Delete a pet by its ID", tags = {"Pet Management System"})
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
        try {
            petService.deletePet(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @GetMapping
    @Operation(summary = "Get all pets", description = "Retrieve a list of all pets", tags = {"Pet Management System"})
    public ResponseEntity<List<PetDTO>> getAllPets() {
        List<PetDTO> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/search")
    @Operation(summary = "Search pets with filters", description = "Search pets using various filters", tags = {"Pet Management System"})
    public ResponseEntity<List<PetDTO>> searchPets(
            @Parameter(description = "Name of the pet") @RequestParam(required = false) String name,
            @Parameter(description = "Species of the pet") @RequestParam(required = false) PetSpeciesEnum species,
            @Parameter(description = "Breed of the pet") @RequestParam(required = false) String breed,
            @Parameter(description = "Tutor of the pet") @RequestParam(required = false) String tutor,
            @Parameter(description = "Email of the tutor") @RequestParam(required = false) String emailTutor,
            @Parameter(description = "Age of the pet") @RequestParam(required = false) LocalDate bithDate,
            @Parameter(description = "Weight of the pet") @RequestParam(required = false) Double weight,
            @Parameter(description = "Color of the pet") @RequestParam(required = false) String color,
            @Parameter(description = "Description of the pet") @RequestParam(required = false) String description) {

        List<PetDTO> pets = petService.searchPets(name, species, breed, tutor, emailTutor, bithDate, weight, color, description);

        return ResponseEntity.ok(pets);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiException(ExternalApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("External API error: " + ex.getMessage());
    }

    @ExceptionHandler(FilterException.class)
    public ResponseEntity<String> handleFilterException(FilterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Filter error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}