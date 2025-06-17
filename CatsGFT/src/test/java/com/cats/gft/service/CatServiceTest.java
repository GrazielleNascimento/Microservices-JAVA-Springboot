package com.cats.gft.service;

import com.cats.gft.catapi.CatResponseApiDto;
import com.cats.gft.catapi.CatResponseImageApiDto;
import com.cats.gft.dto.CatDto;
import com.cats.gft.exception.ResourceNotFoundException;
import com.cats.gft.model.CatModel;
import com.cats.gft.repository.CatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatServiceTest {

    @Mock
    private CatRepository catRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CatService catService;

    private CatModel cat;
    private CatDto catDto;

    @BeforeEach
    void setUp() {
        // PREPARAÇÃO: Criando objetos de teste

        MockitoAnnotations.openMocks(this);
        cat = new CatModel();
        cat.setId(1L);
        cat.setName("Rayka");
        cat.setBreed("siames");
        cat.setImageUrl("image.jpg");

        catDto = new CatDto("Rayka", "siames", "image.jpg");
    }

    @Test
    void testCreateCat_Success() {
        // PREPARAÇÃO
        when(catRepository.save(any(CatModel.class))).thenReturn(cat);
        CatDto catDto = CatDto.builder()
                .name("Rayka")
                .breed("Pastor Suiço")
                .imageUrl("image.jpg")
                .build();

        // AÇÃO
        CatModel createdCat = catService.createCat(catDto);

        // VALIDAÇÃO
        assertNotNull(createdCat);
        assertEquals(cat.getId(), createdCat.getId());
        verify(catRepository, times(1)).save(any(CatModel.class));
    }

    @Test
    void testGetCatById_Success() {
        // PREPARAÇÃO
        when(catRepository.findById(1L)).thenReturn(Optional.of(cat));

        // AÇÃO
        CatModel foundCat = catService.getCatById(1L);

        // VALIDAÇÃO
        assertNotNull(foundCat);
        assertEquals(cat.getId(), foundCat.getId());
        verify(catRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCatById_NotFound() {
        // PREPARAÇÃO
        when(catRepository.findById(1L)).thenReturn(Optional.empty());

        // AÇÃO e VALIDAÇÃO
        CatModel catById = catService.getCatById(1L);
        assertNull(catById);

        verify(catRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllCats_Success() {
        // PREPARAÇÃO
        when(catRepository.findAll()).thenReturn(List.of(cat));

        // AÇÃO
        List<CatModel> cats = catService.getAllCats();

        // VALIDAÇÃO
        assertFalse(cats.isEmpty());
        assertEquals(1, cats.size());
        verify(catRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCat_Success() {
        // PREPARAÇÃO
        when(catRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(catRepository.save(any(CatModel.class))).thenReturn(cat);

        CatDto catDto = CatDto.builder()
                .name("Rayka")
                .breed("Pastor Suiço")
                .imageUrl("image.jpg")
                .build();

        // AÇÃO
        CatModel updatedCat = catService.updateCat(1L, catDto);

        // VALIDAÇÃO
        assertNotNull(updatedCat);
        assertEquals(cat.getId(), updatedCat.getId());
        verify(catRepository, times(1)).save(any(CatModel.class));
    }

    @Test
    void testDeleteCat_Success() {
        // PREPARAÇÃO
        when(catRepository.findById(1L)).thenReturn(Optional.of(cat));
        doNothing().when(catRepository).delete(cat);

        // AÇÃO
        catService.deleteCat(1L);

        // VALIDAÇÃO
        verify(catRepository, times(1)).delete(cat);
    }

    @Test
    void testGetCatsByBreed_Success() {
        // PREPARAÇÃO
        when(catRepository.findByBreed("Labrador")).thenReturn(List.of(cat));

        // AÇÃO
        List<CatModel> cats = catService.getCatsByBreed("Labrador");

        // VALIDAÇÃO
        assertFalse(cats.isEmpty());
        verify(catRepository, times(1)).findByBreed("Labrador");
    }

    @Test
    public void shouldRequestCatByBreedNameWithSuccess() {
        // preparacao
        String breedName = "American Shorthair";
        CatResponseImageApiDto catResponseImageApiDto = new CatResponseImageApiDto();
        catResponseImageApiDto.setUrl("https://cdn2.thecatapi.com/images/MTYwNjQwMg.jpg");
        catResponseImageApiDto.setId("MTYwNjQwMg");
        catResponseImageApiDto.setWidth(500);
        catResponseImageApiDto.setHeight(333);

        when(catService.fetchImagesByBreedId(breedName)).thenReturn(catResponseImageApiDto);

        // acao
        CatResponseApiDto catResponseApiDto = catService.fetchCatByBreed(breedName);

        // validacao
        assertNotNull(catResponseApiDto);
    }


}