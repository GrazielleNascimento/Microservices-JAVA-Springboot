package br.com.petsgft.api.pets.dto.factory;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.dto.PetTutorDTO;
import br.com.petsgft.api.pets.model.PetModel;

import java.util.UUID;

public class PetDTOFactory {

    public static PetDTO createPetDTOFromPetModel(PetModel petModel) {

        PetTutorDTO tutor = PetTutorDTO.builder()
                .id(petModel.getTutor().getId())
                .name(petModel.getTutor().getName())
                .email(petModel.getTutor().getEmail())
                .build();

        return PetDTO.builder()
                .id(petModel.getId())
                .name(petModel.getName())
                .species(petModel.getSpecies())
                .breed(petModel.getBreed())
                .birthDate(petModel.getBirthDate())
                .weight(petModel.getWeight())
                .color(petModel.getColor())
                .description(petModel.getDescription())
                .imageUrl(petModel.getImageUrl())
                .tutor(tutor)
                .build();
    }

    public static PetDTO createPetDTOFromPetModelToPetResponseInfo(PetModel pet, UUID correlationId) {
        PetTutorDTO tutor = PetTutorDTO.builder()
                .id(pet.getTutor().getId())
                .name(pet.getTutor().getName())
                .email(pet.getTutor().getEmail())
                .build();

        return PetDTO.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .birthDate(pet.getBirthDate())
                .weight(pet.getWeight())
                .color(pet.getColor())
                .description(pet.getDescription())
                .imageUrl(pet.getImageUrl())
                .tutor(tutor)
                .correlationId(correlationId)
                .build();
    }
}
