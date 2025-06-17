package br.com.msnotificationemail.notification.dto;

import br.com.msnotificationemail.notification.entity.AppointmentStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("petId")
    private Integer petId;

    @JsonProperty("petName")
    private String petName;

    @JsonProperty("dateAppointment")
    private String appointmentDate;

    @JsonProperty("status")
    private AppointmentStatusEnum status;

    @JsonProperty("tutorId")
    private Integer tutorId;

    @JsonProperty("tutorName")
    private String tutorName;

    @JsonProperty("tutorEmail")
    private String tutorEmail;

    @JsonProperty("description")
    private String description;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}