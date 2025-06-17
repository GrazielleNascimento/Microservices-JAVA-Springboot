package br.com.msappointment.api.appointment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointment")
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pet_id")
    private Integer petId;

    @Column(name = "pet_name")
    private String petName;

    @Column(name = "care_type")
    @Enumerated(EnumType.STRING)
    private AppointmentTypeEnum careType;

    @Column(name = "description")
    private String description;

    @Column(name = "tutor_id")
    private Integer tutorId;

    @Column(name = "tutor_name")
    private String tutorName;

    @Column(name = "tutor_email")
    private String tutorEmail;

    @Column(name = "date_appointment")
    private LocalDateTime dateAppointment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatusEnum status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
