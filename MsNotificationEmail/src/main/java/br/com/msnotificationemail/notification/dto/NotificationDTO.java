package br.com.msnotificationemail.notification.dto;

import br.com.msnotificationemail.notification.entity.NotificationEntity;
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
public class NotificationDTO {

    private Integer id;
    private Integer appointmentId;
    private Integer petId;
    private String petName;
    private Integer tutorId;
    private String tutorName;
    private String tutorEmail;
    private String emailSubject;
    private String emailContent;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime sentAt;

    private String status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime readAt;

    private String errorMessage;
    private String notificationType;
    private NotificationEntity.NotificationReadStatus readStatus;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static NotificationDTO fromEntity(NotificationEntity entity) {
        return NotificationDTO.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointmentId())
                .petId(entity.getPetId())
                .petName(entity.getPetName())
                .tutorId(entity.getTutorId())
                .tutorName(entity.getTutorName())
                .tutorEmail(entity.getTutorEmail())
                .emailSubject(entity.getEmailSubject())
                .emailContent(entity.getEmailContent())
                .sentAt(entity.getSentAt())
                .status(entity.getStatus().name())
                .readStatus(entity.getReadStatus())
                .readAt(entity.getReadAt())
                .errorMessage(entity.getErrorMessage())
                .notificationType(entity.getNotificationType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}