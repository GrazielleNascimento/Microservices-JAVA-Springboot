package br.com.msappointment.api.appointment.dto;

import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;
import br.com.msappointment.api.appointment.model.AppointmentTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AppointmentPendingDTO {

    @JsonProperty("correlationalId")
    private UUID correlationalId;

    @JsonProperty("petId")
    private Integer petId;

    @JsonProperty("careType")
    private AppointmentTypeEnum careType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private AppointmentStatusEnum status;

    @JsonProperty("dateAppointment")
    private LocalDateTime dateAppointment;

}
