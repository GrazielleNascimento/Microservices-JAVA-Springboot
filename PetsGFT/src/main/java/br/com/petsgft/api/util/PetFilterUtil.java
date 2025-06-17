package br.com.petsgft.api.util;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.dto.factory.PetDTOFactory;
import br.com.petsgft.api.exception.FilterException;
import br.com.petsgft.api.pets.model.PetModel;
import br.com.petsgft.api.pets.model.PetSpeciesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PetFilterUtil {

    private static final Logger logger = LoggerFactory.getLogger(PetFilterUtil.class);

    public static List<PetDTO> filterPets(List<PetModel> pets, String name, PetSpeciesEnum species, String breed, String tutor,
                                          String emailTutor, LocalDate birthDate, Double weight, String color, String description) {

        logger.info("Filtering pets with filters - name: {}, species: {}, breed: {}, tutor: {}, emailTutor: {}, age: {}," +
                        " weight: {}, color: {}, description: {}",
                name, species, breed, tutor, emailTutor, birthDate, weight, color, description);


        try {
            List<PetDTO> filteredPets = pets.stream()
                    .filter(pet -> (name == null || pet.getName().equalsIgnoreCase(name)) &&
                            (species == null || pet.getSpecies().equals(species)) &&
                            (breed == null || pet.getBreed().equalsIgnoreCase(breed)) &&
                            (tutor == null || pet.getTutor().getName().equalsIgnoreCase(tutor)) &&
                            (emailTutor == null || pet.getTutor().getEmail().equalsIgnoreCase(emailTutor)) &&
                            (birthDate == null || pet.getBirthDate() == birthDate) &&
                            (weight == null || pet.getWeight() == weight) &&
                            (color == null || pet.getColor().equalsIgnoreCase(color)) &&
                            (description == null || pet.getDescription().equalsIgnoreCase(description)))
                    .map(PetDTOFactory::createPetDTOFromPetModel)
                    .collect(Collectors.toList());

            logger.info("Filtered {} pets", filteredPets.size());

            return filteredPets;

        } catch (Exception e) {
            logger.error("Error filtering pets", e);
            throw new FilterException("Error filtering pets", e);
        }
    }
}