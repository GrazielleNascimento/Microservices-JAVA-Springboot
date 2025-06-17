package com.dogs.gft.service;

import com.dogs.gft.dto.DogDto;
import com.dogs.gft.dto.DogDtoRecord;
import com.dogs.gft.exception.ResourceNotFoundException;
import com.dogs.gft.model.DogModel;
import com.dogs.gft.repository.DogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DogService dogService;

    private DogModel dog;
    private DogDtoRecord dogDto;

    @BeforeEach
    void setUp() {
        // PREPARAÇÃO: Criando objetos de teste
        dog = new DogModel();
        dog.setId(1L);
        dog.setName("Rayka");
        dog.setBreed("Pastor Suiço");
        dog.setImageUrl("image.jpg");

        dogDto = new DogDtoRecord("Rayka", "Pastor Suiço", "image.jpg");
    }

    @Test
    void testCreateDog_Success() {
        // PREPARAÇÃO
        when(dogRepository.save(any(DogModel.class))).thenReturn(dog);
        DogDto dogDto = DogDto.builder()
                .name("Rayka")
                .breed("Pastor suiço")
                .imageUrl("image.jpg")
                .build();


        // AÇÃO
        DogModel createdDog = dogService.createDog(dogDto);

        // VALIDAÇÃO
        assertNotNull(createdDog);
        assertEquals(dog.getId(), createdDog.getId());
        verify(dogRepository, times(1)).save(any(DogModel.class));
    }

    @Test
    void testGetDogById_Success() {
        // PREPARAÇÃO
        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));

        // AÇÃO
        DogModel foundDog = dogService.getDogById(1L);

        // VALIDAÇÃO
        assertNotNull(foundDog);
        assertEquals(dog.getId(), foundDog.getId());
        verify(dogRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDogById_NotFound() {
        // PREPARAÇÃO
        when(dogRepository.findById(1L)).thenReturn(Optional.empty());

        // AÇÃO e VALIDAÇÃO
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> dogService.getDogById(1L));
        assertEquals("Dog not found with ID: 1", exception.getMessage());

        verify(dogRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllDogs_Success() {
        // PREPARAÇÃO
        when(dogRepository.findAll()).thenReturn(List.of(dog));

        // AÇÃO
        List<DogModel> dogs = dogService.getAllDogs();

        // VALIDAÇÃO
        assertFalse(dogs.isEmpty());
        assertEquals(1, dogs.size());
        verify(dogRepository, times(1)).findAll();
    }

    @Test
    void testUpdateDog_Success() {
        // PREPARAÇÃO
        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));
        when(dogRepository.save(any(DogModel.class))).thenReturn(dog);

        DogDto dogDto = DogDto.builder()
                .name("Rayka")
                .breed("Pastor suiço")
                .imageUrl("image.jpg")
                .build();

        // AÇÃO
        DogModel updatedDog = dogService.updateDog(1L, dogDto);


        // VALIDAÇÃO
        assertNotNull(updatedDog);
        assertEquals(dog.getId(), updatedDog.getId());
        verify(dogRepository, times(1)).save(any(DogModel.class));
    }

    @Test
    void testDeleteDog_Success() {
        // PREPARAÇÃO
        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));
        doNothing().when(dogRepository).delete(dog);

        // AÇÃO
        dogService.deleteDog(1L);

        // VALIDAÇÃO
        verify(dogRepository, times(1)).delete(dog);
    }

    @Test
    void testGetDogsByBreed_Success() {
        // PREPARAÇÃO
        when(dogRepository.findByBreed("Labrador")).thenReturn(List.of(dog));

        // AÇÃO
        List<DogModel> dogs = dogService.getDogsByBreed("Labrador");

        // VALIDAÇÃO
        assertFalse(dogs.isEmpty());
        verify(dogRepository, times(1)).findByBreed("Labrador");
    }


}