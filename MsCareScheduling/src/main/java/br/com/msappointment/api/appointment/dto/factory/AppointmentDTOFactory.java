package br.com.msappointment.api.appointment.dto.factory;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.model.AppointmentModel;
import br.com.msappointment.api.appointment.model.AppointmentPendingModel;
import br.com.msappointment.integrations.rabbitmq.dto.PetDTO;

public class AppointmentDTOFactory {

    public static AppointmentDTO createAppointmentDTOFromAppointmentModel(AppointmentModel appointmentModel) {
        return AppointmentDTO.builder()
                .id(appointmentModel.getId())
                .petId(appointmentModel.getPetId())
                .petName(appointmentModel.getPetName())
                .careType(appointmentModel.getCareType())
                .description(appointmentModel.getDescription())
                .tutorId(appointmentModel.getTutorId())
                .tutorName(appointmentModel.getTutorName())
                .tutorEmail(appointmentModel.getTutorEmail())
                .status(appointmentModel.getStatus())
                .dateAppointment(appointmentModel.getDateAppointment())
                .build();
    }

    public static AppointmentDTO createAppointmentDTOFromAppointmentPendingDTO(AppointmentPendingModel appointmentPending, PetDTO petDTO) {
        return AppointmentDTO.builder()
                .petId(appointmentPending.getPetId())
                .petName(petDTO.getName())
                .careType(appointmentPending.getCareType())
                .description(appointmentPending.getDescription())
                .status(appointmentPending.getStatus())
                .dateAppointment(appointmentPending.getDateAppointment())
                .tutorId(petDTO.getTutor().getId())
                .tutorEmail(petDTO.getTutor().getEmail())
                .tutorName(petDTO.getTutor().getName())
                .build();
    }

}
