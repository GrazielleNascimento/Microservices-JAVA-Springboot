package br.com.petsgft.integration.rabbitmq.service;

import br.com.petsgft.api.pets.dto.PetDTO;
import br.com.petsgft.api.exception.ExternalApiException;
import br.com.petsgft.api.pets.repository.PetRepository;
import br.com.petsgft.integration.rabbitmq.config.RabbitMQConfig;
import br.com.petsgft.integration.rabbitmq.producer.RabbitMQProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    private final PetRepository petRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQService(
            PetRepository petRepository,
            RabbitMQProducer rabbitMQProducer,
            ObjectMapper objectMapper

    ) {
        this.petRepository = petRepository;
        this.rabbitMQProducer = rabbitMQProducer;
        this.objectMapper = objectMapper;
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendMessagePetCreated(PetDTO petDTO) {
        try {
            String message = objectMapper.writeValueAsString(petDTO);

            rabbitMQProducer.createMessage(RabbitMQConfig.PET_EXCHANGE_NAME, RabbitMQConfig.PET_CREATED_ROUTING_KEY, message);
            logger.info("Message sent to RabbitMQ: {}", message);

        } catch (Exception e) {
            logger.error("Error sending message to RabbitMQ", e);
            throw new ExternalApiException("Error sending message to RabbitMQ", e);
        }
    }

    public void sendMessagePetResponseInfo(PetDTO petDTO) {
        try {
            String message = objectMapper.writeValueAsString(petDTO);

            rabbitMQProducer.createMessage(RabbitMQConfig.PET_EXCHANGE_NAME, RabbitMQConfig.PET_RESPONSE_ROUTING_KEY, message);
            logger.info("Message sent to RabbitMQ: {}", message);

        } catch (Exception e) {
            logger.error("Error sending message to RabbitMQ", e);
            throw new ExternalApiException("Error sending message to RabbitMQ", e);
        }
    }

}
