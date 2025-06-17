package br.com.petsgft.api.pets.model.factory;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.model.PetModel;
import br.com.petsgft.api.pets.model.PetTutorModel;

public class PetModelFactory {

    public static PetModel createPetModelFromPetDTO(PetDTO petDTO) {
        PetTutorModel tutor = PetTutorModel.builder()
                .name(petDTO.getTutor().getName())
                .email(petDTO.getTutor().getEmail())
                .build();

        return PetModel.builder()
                .name(petDTO.getName())
                .species(petDTO.getSpecies())
                .breed(petDTO.getBreed())
                .birthDate(petDTO.getBirthDate())
                .weight(petDTO.getWeight())
                .color(petDTO.getColor())
                .description(petDTO.getDescription())
                .imageUrl(petDTO.getImageUrl())
                .tutor(tutor)
                .build();
    }

}
