package br.com.petsgft.api.pets.service;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.dto.PetTutorDTO;
import br.com.petsgft.api.pets.model.PetSpeciesEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PetServiceTest {

    @Autowired
    PetService petService;

    @Test
    public void shouldReturnPetById() {

        // prepare
        PetDTO petDTOMock = PetDTO.builder()
                .name("Buddy")
                .species(PetSpeciesEnum.valueOf("CAT"))
                .breed("Golden Retriever")
                .birthDate(LocalDate.of(2025,3,4))
                .weight(30.0)
                .color("Golden")
                .description("Friendly cat")
                .tutor(PetTutorDTO.builder()
                        .name("John Doe")
                        .email("john.doe@example.com")
                        .build()
                )
                .build();

        // action
        PetDTO petDTO = petService.createPet(petDTOMock);

        // assertion
        assertNotNull(petDTO);
        assertNotNull(petDTO.getImageUrl());
    }

}
