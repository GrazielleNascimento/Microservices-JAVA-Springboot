package com.cats.gft.controller;

import com.cats.gft.api.Response;
import com.cats.gft.catapi.CatResponseImageApiDto;
import com.cats.gft.dto.CatDto;
import com.cats.gft.exception.ResourceNotFoundException;
import com.cats.gft.model.CatModel;
import com.cats.gft.service.CatService;
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
class CatControllerTest {

    @Mock
    private CatService catService;

    @InjectMocks
    private CatController catController;

    private CatModel cat;
    private CatDto catDto;

    @BeforeEach
    void setUp() {
        // Preparação dos objetos de teste
        cat = new CatModel();
        cat.setId(1L);
        cat.setName("Rayka");
        cat.setBreed("siames");
        cat.setImageUrl("image.jpg");

        catDto = new CatDto("Rayka", "siames", "image.jpg");
    }

    @Test
    void testCreateCat_Success() {
        // PREPARAÇÃO: Configura o mock para retornar um CatModel ao criar um gato
        when(catService.createCat(any(CatDto.class))).thenReturn(cat);
        CatDto catDto = CatDto.builder()
                .name("Rayka")
                .breed("Pastor suiço")
                .imageUrl("image.jpg")
                .build();

        // AÇÃO: Chama o método da controller
        ResponseEntity<CatModel> response = catController.createCat(catDto);

        // VALIDAÇÃO: Verifica se o retorno está correto
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cat.getId(), response.getBody().getId());

        verify(catService, times(1)).createCat(any(CatDto.class));
    }

    @Test
    void testGetCatById_Success() {
        // PREPARAÇÃO
        when(catService.getCatById(1L)).thenReturn(cat);

        // AÇÃO
        ResponseEntity<CatModel> response = catController.getCatById(1L);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cat.getId(), response.getBody().getId());

        verify(catService, times(1)).getCatById(1L);
    }

    @Test
    void testGetCatById_NotFound() {
        // PREPARAÇÃO
        when(catService.getCatById(1L)).thenThrow(new ResourceNotFoundException("Cat not found"));

        // AÇÃO e VALIDAÇÃO
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> catController.getCatById(1L));
        assertEquals("Cat not found", exception.getMessage());

        verify(catService, times(1)).getCatById(1L);
    }

    @Test
    void testGetAllCats_Success() {
        // PREPARAÇÃO
        when(catService.getAllCats()).thenReturn(List.of(cat));

        // AÇÃO
        ResponseEntity<List<CatModel>> response = catController.getAllCats();

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());

        verify(catService, times(1)).getAllCats();
    }

    @Test
    void testUpdateCat_Success() {
        // PREPARAÇÃO
        when(catService.updateCat(eq(1L), any(CatDto.class))).thenReturn(cat);
        CatDto catDto = CatDto.builder()
                .name("Rayka")
                .breed("Pastor suiço")
                .imageUrl("image.jpg")
                .build();

        // AÇÃO
        ResponseEntity<Response<CatModel>> response = catController.updateCat(1L, catDto);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cat.getId(), response.getBody().getData().getId());

        verify(catService, times(1)).updateCat(eq(1L), any(CatDto.class));
    }

    @Test
    void testDeleteCat_Success() {
        // PREPARAÇÃO
        doNothing().when(catService).deleteCat(1L);

        // AÇÃO
        ResponseEntity<String> response = catController.deleteCat(1L);

        // VALIDAÇÃO
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(catService, times(1)).deleteCat(1L);
    }

    @Test
    void testGetCatsByBreed_Success() {
        // PREPARAÇÃO
        when(catService.getCatsByBreed("Labrador")).thenReturn(List.of(cat));

        // AÇÃO
        ResponseEntity<Response<List<CatModel>>> response = catController.getCatsByBreed("Labrador");

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getData().isEmpty());

        verify(catService, times(1)).getCatsByBreed("Labrador");
    }

    @Test
    void testGetCatsByName_Success() {
        // PREPARAÇÃO
        when(catService.getCatsByName("Rex")).thenReturn(List.of(cat));

        // AÇÃO
        ResponseEntity<Response<List<CatModel>>> response = catController.getCatsByName("Rex");

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getData().isEmpty());

        verify(catService, times(1)).getCatsByName("Rex");
    }

    @Test
    void testFetchAllBreedsFromExternal_Success() {
        // PREPARAÇÃO
        String breedsResponse = "Labrador, Bulldog, Poodle";
        when(catService.fetchAllBreedsFromExternal()).thenReturn(breedsResponse);

        // AÇÃO
        ResponseEntity<String> response = catController.fetchAllBreedsFromExternal();

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(breedsResponse, response.getBody());

        verify(catService, times(1)).fetchAllBreedsFromExternal();
    }

    @Test
    void testFetchImagesByBreed_Success() {
        // PREPARAÇÃO
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("breed", "Labrador");
        imageMap.put("imageUrl", "image.jpg");
        CatResponseImageApiDto catResponseImageApiDto = new CatResponseImageApiDto();
        catResponseImageApiDto.setId("asho");
        catResponseImageApiDto.setUrl("http://grazyCatImage.com.br/GraziAmor.jpg");

        when(catService.fetchImagesByBreedId("asho")).thenReturn(catResponseImageApiDto);

        // AÇÃO
        ResponseEntity<Response<CatResponseImageApiDto>> response = catController.fetchImagesByBreedId("asho");

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(catService, times(1)).fetchImagesByBreedId("asho");
    }

    @Test
    void testFetchCatBreeds_Success() {
        // PREPARAÇÃO
        String fetchResponse = "Cat breeds fetched";
        when(catService.fetchAndStoreBreeds(true)).thenReturn(fetchResponse);

        // AÇÃO
        ResponseEntity<String> response = catController.fetchCatBreeds(true);

        // VALIDAÇÃO
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fetchResponse, response.getBody());

        verify(catService, times(1)).fetchAndStoreBreeds(true);
    }
}