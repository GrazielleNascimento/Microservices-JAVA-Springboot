package br.com.petsgft.api.pets.service;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.model.PetSpeciesEnum;
import br.com.petsgft.api.pets.model.factory.PetModelFactory;
import br.com.petsgft.api.pets.dto.factory.PetDTOFactory;
import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.api.exception.FilterException;
import br.com.petsgft.api.pets.model.PetModel;
import br.com.petsgft.api.pets.model.PetTutorModel;
import br.com.petsgft.integration.catsapi.service.CatApiService;
import br.com.petsgft.integration.dogsapi.service.DogApiService;
import br.com.petsgft.integration.rabbitmq.service.RabbitMQService;
import br.com.petsgft.api.pets.repository.PetRepository;
import br.com.petsgft.api.util.PetFilterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final CatApiService catApiService;
    private final DogApiService dogApiService;
    private final RabbitMQService rabbitMQService;

    @Autowired
    public PetService(
            PetRepository petRepository,
            CatApiService catApiService,
            DogApiService dogApiService,
            RabbitMQService rabbitMQService
    ) {
        this.petRepository = petRepository;
        this.catApiService = catApiService;
        this.dogApiService = dogApiService;
        this.rabbitMQService = rabbitMQService;
    }

    public PetDTO createPet(PetDTO petDTO) {

        logger.info("Creating a new pet with name: {}", petDTO.getName());
        try {

            petDTO.setImageUrl(fetchImageBySpecies(petDTO.getSpecies(), petDTO.getBreed()));

            PetModel pet = PetModelFactory.createPetModelFromPetDTO(petDTO);
            pet = petRepository.save(pet);

            rabbitMQService.sendMessagePetCreated(PetDTOFactory.createPetDTOFromPetModel(pet));

            logger.info("Pet with ID: {} created successfully", pet.getId());
            return PetDTOFactory.createPetDTOFromPetModel(pet);

        } catch (Exception e) {
            logger.error("Error creating pet", e);
            throw new ExternalApiException("Error creating pet", e);
        }
    }

    public List<PetDTO> getAllPets() {
        logger.info("Fetching all pets");
        return petRepository.findAll().stream()
                .map(PetDTOFactory::createPetDTOFromPetModel)
                .collect(Collectors.toList());
    }

    public List<PetDTO> searchPets(String name, PetSpeciesEnum species, String breed, String tutor, String emailTutor,
                                   LocalDate bithDate, Double weight, String color, String description) {

        logger.info("Searching pets with filters - name: {}, species: {}, breed: {}, tutor: {}, emailTutor: {}, age: {}," +
                        " weight: {}, color: {}, description: {}",
                name, species, breed, tutor, emailTutor, bithDate, weight, color, description);

        try {
            List<PetModel> pets = petRepository.findAll();
            return PetFilterUtil.filterPets(pets, name, species, breed, tutor, emailTutor, bithDate, weight, color, description);

        } catch (FilterException e) {
            logger.error("Error searching pets", e);
            throw new ExternalApiException("Error searching pets", e);
        }
    }

    public PetDTO getPetById(Integer id) {
        logger.info("Fetching pet with ID: {}", id);
        PetModel pet = petRepository.findById(id).orElseThrow(() -> new ExternalApiException("Pet not found", null));
        return PetDTOFactory.createPetDTOFromPetModel(pet);
    }

    public PetDTO updatePet(Integer id, PetDTO petDTO) {
        logger.info("Updating pet with ID: {}", id);

        // busca o pet pelo id no banco que vai ser atualizado
        PetModel pet = petRepository.findById(id).orElseThrow(() -> new ExternalApiException("Pet not found", null));

        // prepara o pet encontrado, com os valores atualizados, para salvar no banco
        pet.setName(petDTO.getName());
        pet.setSpecies(petDTO.getSpecies());
        pet.setBreed(petDTO.getBreed());
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setWeight(petDTO.getWeight());
        pet.setColor(petDTO.getColor());
        pet.setDescription(petDTO.getDescription());

        PetTutorModel tutor = PetTutorModel.builder()
                .name(petDTO.getTutor().getName())
                .email(petDTO.getTutor().getEmail())
                .build();
        pet.setTutor(tutor);

        // salva o pet atualizado no banco com os novos valores
        pet = petRepository.save(pet);

        // retorna o uma DTO do pet atualizado
        return PetDTOFactory.createPetDTOFromPetModel(pet);
    }

    public void deletePet(Integer id) {
        logger.info("Deleting pet with ID: {}", id);
        PetModel pet = petRepository.findById(id).orElseThrow(() -> new ExternalApiException("Pet not found", null));
        petRepository.delete(pet);
    }

    public String fetchImageBySpecies(PetSpeciesEnum species, String breed) {
        switch (species) {
            case CAT:
                try {
                    return catApiService.fetchImage(breed).getUrl();
                } catch(ExternalApiException e) {
                    logger.error("Error fetching image from Cat API", e);
                }
            case DOG:
                return dogApiService.fetchImage(breed).getImageUrl();
        }
        return "";
    }

    public void getPetForPetRequestAndSendToResponseInfo(PetDTO petDTO) {
        logger.info("Fetching pet with ID: {} for pet request", petDTO.getId());
        PetModel pet = petRepository.findById(petDTO.getId()).orElseThrow(() -> new ExternalApiException("Pet not found", null));

        rabbitMQService.sendMessagePetResponseInfo(PetDTOFactory.createPetDTOFromPetModelToPetResponseInfo(pet, petDTO.getCorrelationId()));
    }

}
