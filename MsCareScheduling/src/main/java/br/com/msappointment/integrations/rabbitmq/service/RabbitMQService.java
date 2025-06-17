package br.com.msappointment.integrations.rabbitmq.service;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.integrations.rabbitmq.config.RabbitMQConfig;
import br.com.msappointment.integrations.rabbitmq.dto.PetDTO;
import br.com.msappointment.integrations.rabbitmq.producer.RabbitMQProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RabbitMQService {

    private final RabbitMQProducer rabbitMQProducer;

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQService(
            RabbitMQProducer rabbitMQProducer,
            ObjectMapper objectMapper
    ) {
        this.rabbitMQProducer = rabbitMQProducer;
        this.objectMapper = objectMapper;
    }

    public void requestPetInfo(PetDTO petDTO) {
        logger.info("Requesting pet info for pet ID: {} with correlational ID: {}", petDTO.getId(), petDTO.getCorrelationId());
        try {
            String jsonMessage = objectMapper.writeValueAsString(petDTO);
            rabbitMQProducer.createMessage(
                    RabbitMQConfig.PET_EXCHANGE_NAME,
                    RabbitMQConfig.PET_INFO_REQUEST_ROUTING_KEY,
                    jsonMessage
            );
        } catch (Exception e) {
            logger.error("Error requesting pet info", e);
        }
    }

    public void sendMessageAppointmentStatus(AppointmentDTO appointmentDTO) {
        logger.info("Sending message to Queue {} for appointment created", RabbitMQConfig.APPOINTMENT_STATUS_QUEUE_NAME);
        try {
            String jsonMessage = objectMapper.writeValueAsString(appointmentDTO);
            rabbitMQProducer.createMessage(
                    RabbitMQConfig.APPOINTMENT_EXCHANGE_NAME,
                    RabbitMQConfig.APPOINTMENT_STATUS_ROUTING_KEY,
                    jsonMessage
            );
        } catch (Exception e) {
            logger.error("Error sending message to RabbitMQ", e);
        }
    }
}


