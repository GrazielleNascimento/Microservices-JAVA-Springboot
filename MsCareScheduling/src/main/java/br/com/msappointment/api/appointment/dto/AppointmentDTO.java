package br.com.msappointment.api.appointment.dto;

import br.com.msappointment.api.appointment.model.AppointmentStatusEnum;
import br.com.msappointment.api.appointment.model.AppointmentTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDTO {


    @JsonProperty("id")
    private Integer id;

    @NotNull
    @JsonProperty("petId")
    private Integer petId;

    @JsonProperty("petName")
    private String petName;

    @JsonProperty("careType")
    private AppointmentTypeEnum careType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tutorId")
    private Integer tutorId;

    @JsonProperty("tutorEmail")
    private String tutorEmail;

    @JsonProperty("tutorName")
    private String tutorName;

    @JsonProperty("status")
    private AppointmentStatusEnum status;

    @JsonProperty("dateAppointment")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "America/Sao_Paulo"
    )
    private LocalDateTime dateAppointment;
}
