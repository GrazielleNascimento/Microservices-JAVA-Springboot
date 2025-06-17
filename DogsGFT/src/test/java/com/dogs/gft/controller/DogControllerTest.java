package com.dogs.gft.controller;

import com.dogs.gft.dto.DogDto;
import com.dogs.gft.dto.DogDtoRecord;
import com.dogs.gft.exception.ResourceNotFoundException;
import com.dogs.gft.model.DogModel;
import com.dogs.gft.service.DogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogControllerTest {

    @Mock
    private DogService dogService;

    @InjectMocks
    private DogController dogController;

    private DogModel dog;
    private DogDtoRecord dogDto;


    @BeforeEach
    void setUp() {
        // Preparação dos objetos de teste
        dog = new DogModel();
        dog.setId(1L);
        dog.setName("Rayka");
        dog.setBreed("Pastor suiço");
        dog.setImageUrl("image.jpg");

        dogDto = new DogDtoRecord("Rayka", "Pastor suiço", "image.jpg");
    }

    @Test
    void testCreateDog_Success() {
        // PREPARAÇÃO: Configura o mock para retornar um dogModel ao criar um cachorro
        when(dogService.createDog(any(DogDto.class))).thenReturn(dog);
        DogDto dogDto = DogDto.builder()
                .name("Rayka")
                .breed("Pastor suiço")
                .imageUrl("image.jpg")
                .build();


        // AÇÃO: Chama o método da controller
        ResponseEntity<DogModel> response = dogController.createDog(dogDto);

        // VALIDAÇÃO: Verifica se o retorno está correto
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dog.getId(), response.getBody().getId());

        verify(dogService, times(1)).createDog(any(DogDto.class));
    }

    @Test
    void testGetDogById_Success() {
        // PREPARAÇÃO
        when(dogService.getDogById(1L)).thenReturn(dog);

        // AÇÃO
        ResponseEntity<DogModel> response = dogController.getDogById(1L);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dog.getId(), response.getBody().getId());

        verify(dogService, times(1)).getDogById(1L);
    }

    @Test
    void testGetDogById_NotFound() {
        // PREPARAÇÃO
        when(dogService.getDogById(1L)).thenThrow(new ResourceNotFoundException("Dog not found"));

        // AÇÃO e VALIDAÇÃO
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> dogController.getDogById(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertInstanceOf(ResourceNotFoundException.class, exception.getCause());
        assertEquals("Dog not found", exception.getReason());

        verify(dogService, times(1)).getDogById(1L);
    }

    @Test
    void testGetAllDogs_Success() {
        // PREPARAÇÃO
        when(dogService.getAllDogs()).thenReturn(List.of(dog));

        // AÇÃO
        ResponseEntity<List<DogModel>> response = dogController.getAllDogs();

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());

        verify(dogService, times(1)).getAllDogs();
    }

    @Test
    void testUpdateDog_Success() {
        // PREPARAÇÃO
        when(dogService.updateDog(eq(1L), any(DogDto.class))).thenReturn(dog);
        DogDto dogDto = DogDto.builder()
                .name("Rayka")
                .breed("Pastor suiço")
                .imageUrl("image.jpg")
                .build();

        // AÇÃO
        ResponseEntity<DogModel> response = dogController.updateDog(1L, dogDto);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dog.getId(), response.getBody().getId());

        verify(dogService, times(1)).updateDog(eq(1L), any(DogDto.class));
    }

    @Test
    void testDeleteDog_Success() {
        // PREPARAÇÃO
        doNothing().when(dogService).deleteDog(1L);

        // AÇÃO
        ResponseEntity<Void> response = dogController.deleteDog(1L);

        // VALIDAÇÃO
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(dogService, times(1)).deleteDog(1L);
    }

    @Test
    void testGetDogsByBreed_Success() {
        // PREPARAÇÃO
        when(dogService.getDogsByBreed("Labrador")).thenReturn(List.of(dog));

        // AÇÃO
        ResponseEntity<List<DogModel>> response = dogController.getDogsByBreed("Labrador");

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());

        verify(dogService, times(1)).getDogsByBreed("Labrador");
    }

    @Test
    void testGetDogsByName_Success() {
        // PREPARAÇÃO
        when(dogService.getDogsByName("Rex")).thenReturn(List.of(dog));

        // AÇÃO
        ResponseEntity<List<DogModel>> response = dogController.getDogsByName("Rex");

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());

        verify(dogService, times(1)).getDogsByName("Rex");
    }

    @Test
    void testFetchAllBreedsFromExternal_Success() {
        // PREPARAÇÃO
        String breedsResponse = "Labrador, Bulldog, Poodle";
        when(dogService.fetchAllBreedsFromExternal()).thenReturn(breedsResponse);

        // AÇÃO
        ResponseEntity<String> response = dogController.fetchAllBreedsFromExternal();

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(breedsResponse, response.getBody());

        verify(dogService, times(1)).fetchAllBreedsFromExternal();
    }

    @Test
    void testFetchImagesByBreed_Success() {
        // PREPARAÇÃO
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("breed", "Labrador");
        imageMap.put("imageUrl", "image.jpg");

        when(dogService.fetchImagesByBreed("Labrador", 2)).thenReturn(imageMap);

        // AÇÃO
        ResponseEntity<String> response = dogController.fetchImagesByBreed("Labrador", 2);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(dogService, times(1)).fetchImagesByBreed("Labrador", 2);
    }

    @Test
    void testFetchDogBreeds_Success() {
        // PREPARAÇÃO
        String fetchResponse = "Dog breeds fetched";
        when(dogService.fetchAndStoreBreeds(true)).thenReturn(fetchResponse);

        // AÇÃO
        ResponseEntity<String> response = dogController.fetchDogBreeds(true);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fetchResponse, response.getBody());

        verify(dogService, times(1)).fetchAndStoreBreeds(true);
    }
}
