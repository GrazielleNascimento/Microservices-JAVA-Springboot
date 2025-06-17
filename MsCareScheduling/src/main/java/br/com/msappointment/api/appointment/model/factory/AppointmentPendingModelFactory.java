package br.com.msappointment.api.appointment.model.factory;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.model.AppointmentPendingModel;
import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;

import java.util.UUID;

public class AppointmentPendingModelFactory {

    public static AppointmentPendingModel createAppointmentPendingModelFromAppointmentDTO(AppointmentDTO appointmentDTO,
                                                                                          UUID correlationId) {
        return AppointmentPendingModel.builder()
                .careType(appointmentDTO.getCareType())
                .dateAppointment(appointmentDTO.getDateAppointment())
                .status(AppointmentStatusEnum.PENDING)
                .description(appointmentDTO.getDescription())
                .correlationId(correlationId)
                .petId(appointmentDTO.getPetId())
                .build();
    }

}
