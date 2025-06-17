package br.com.msappointment.api.appointment.model.factory;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.dto.AppointmentPendingDTO;
import br.com.msappointment.api.appointment.model.AppointmentPendingModel;

public class AppointmentPendingDTOFactory {
    public static AppointmentPendingDTO createAppointmentPendingDTOFromAppointmentPendingModel(AppointmentPendingModel appointmentPending) {
        return AppointmentPendingDTO.builder()
                .correlationalId(appointmentPending.getCorrelationId())
                .petId(appointmentPending.getPetId())
                .careType(appointmentPending.getCareType())
                .description(appointmentPending.getDescription())
                .dateAppointment(appointmentPending.getDateAppointment())
                .build();
    }
}
