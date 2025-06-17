package br.com.msappointment.integrations.rabbitmq.dto.factory;

import br.com.msappointment.api.appointment.model.AppointmentPendingModel;
import br.com.msappointment.integrations.rabbitmq.dto.PetDTO;

import java.util.UUID;

public class PetDTOFactory {


    public static PetDTO createPetDTOFromApointmentPending(AppointmentPendingModel appointment) {
        return PetDTO.builder()
                .id(appointment.getPetId())
                .correlationId(appointment.getCorrelationId())
                .build();
    }
}
