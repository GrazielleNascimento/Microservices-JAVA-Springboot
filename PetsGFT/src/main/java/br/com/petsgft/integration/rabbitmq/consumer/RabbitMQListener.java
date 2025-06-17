package br.com.petsgft.integration.rabbitmq.consumer;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.pets.service.PetService;
import br.com.petsgft.integration.rabbitmq.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {

    private final PetService petService;

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);

    @Autowired
    public RabbitMQListener(
            PetService petService,
            ObjectMapper objectMapper
    ) {
        this.petService = petService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.PET_INFO_REQUEST_QUEUE_NAME)
    public void handlePetInfoRequest(String petRequested) {
        logger.info("Received pet info request for pet ID: {}", petRequested);
        try {
            PetDTO petDTO = objectMapper.readValue(petRequested, PetDTO.class);
            petService.getPetForPetRequestAndSendToResponseInfo(petDTO);
        } catch (Exception e) {
            logger.error("Error handling pet info request", e);
        }

    }

}
