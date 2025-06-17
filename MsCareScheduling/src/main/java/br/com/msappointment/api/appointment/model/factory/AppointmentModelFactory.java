package br.com.msappointment.api.appointment.model.factory;

import br.com.msappointment.api.appointment.dto.AppointmentDTO;
import br.com.msappointment.api.appointment.model.AppointmentModel;
import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;
import br.com.msappointment.api.appointment.model.AppointmentTypeEnum;
import br.com.msappointment.integrations.rabbitmq.dto.PetDTO;

import java.time.LocalDateTime;

public class AppointmentModelFactory {

    public static AppointmentModel createAppointmentModelFromAppointmentDTO(AppointmentDTO appointmentDTO) {
        return AppointmentModel.builder()
                .petId(appointmentDTO.getPetId())
                .petName(appointmentDTO.getPetName())
                .careType(appointmentDTO.getCareType())
                .description(appointmentDTO.getDescription())
                .tutorId(appointmentDTO.getTutorId())
                .tutorName(appointmentDTO.getTutorName())
                .tutorEmail(appointmentDTO.getTutorEmail())
                .status(appointmentDTO.getStatus())
                .dateAppointment(appointmentDTO.getDateAppointment())
                .build();
    }

    public static AppointmentModel createInitialAppointmentModelForNewPetCreated(PetDTO petDTO,
                                                                                 AppointmentTypeEnum appointmentType,
                                                                                 LocalDateTime dateAppointment) {
        return AppointmentModel.builder()
                .petId(petDTO.getId())
                .petName(petDTO.getName())
                .careType(appointmentType)
                .description(appointmentType.getDescription())
                .tutorId(petDTO.getTutor().getId())
                .tutorName(petDTO.getTutor().getName())
                .tutorEmail(petDTO.getTutor().getEmail())
                .dateAppointment(dateAppointment)
                .status(AppointmentStatusEnum.PENDING)
                .build();
    }

}
