package br.com.msappointment.api.appointment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointment_pending")
public class AppointmentPendingModel {

    @Id
    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "pet_id")
    private Integer petId;

    @Column(name = "care_type")
    @Enumerated(EnumType.STRING)
    private AppointmentTypeEnum careType;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatusEnum status;

    @Column(name = "date_appointment")
    private LocalDateTime dateAppointment;

}
