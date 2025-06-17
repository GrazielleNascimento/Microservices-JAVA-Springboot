package br.com.msappointment.integrations.rabbitmq.consumer;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.service.AppointmentService;
import br.com.msappointment.integrations.rabbitmq.config.RabbitMQConfig;
import br.com.msappointment.integrations.rabbitmq.dto.PetDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {

    private final AppointmentService appointmentService;

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQListener(
            AppointmentService appointmentService,
            ObjectMapper objectMapper
    ) {
        this.appointmentService = appointmentService;
        this.objectMapper = objectMapper;
    }

    // automatic
    @RabbitListener(queues = RabbitMQConfig.PET_CREATED_QUEUE_NAME)
    public void receiveMessagePetCreated(String message) {
        logger.info("Received message from {}: {}", RabbitMQConfig.PET_CREATED_QUEUE_NAME, message);

        try {
            PetDTO petDTO = objectMapper.readValue(message, PetDTO.class);
            appointmentService.createAutomaticAppointmentsForNewPetCreated(petDTO);
        } catch (Exception e) {
            logger.error("Error while parsing message: {}", message);
        }

    }

    // manual
    @RabbitListener(queues = RabbitMQConfig.PET_INFO_RESPONSE_QUEUE_NAME)
    public void receiveMessagePetInfoResponse(String message) {
        logger.info("Received message from {}: {}", RabbitMQConfig.PET_INFO_RESPONSE_QUEUE_NAME, message);

        try {
            PetDTO petDTO = objectMapper.readValue(message, PetDTO.class);
            appointmentService.finalizePendingAppointmentWithPetInfo(petDTO);
        } catch (Exception e) {
            logger.error("Error while parsing message: {}", message);
        }
    }

}
