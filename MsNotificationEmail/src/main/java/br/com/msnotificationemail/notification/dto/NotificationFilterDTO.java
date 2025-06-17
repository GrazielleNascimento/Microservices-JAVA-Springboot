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
public class NotificationFilterDTO {
    private Integer petId;
    private String petName;
    private Integer tutorId;
    private String tutorName;
    private String tutorEmail;
    private String status;
    private String notificationType;
    private String readStatus;

    @JsonProperty("sentAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    @JsonProperty("readAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;

    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonProperty("endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    public NotificationEntity.NotificationStatus getStatusEnum() {
        if (status == null || status.isEmpty()) {
            return null;
        }

        try {
            return NotificationEntity.NotificationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public NotificationEntity.NotificationReadStatus getReadStatusEnum() {
        if (readStatus == null || readStatus.isEmpty()) {
            return null;
        }

        try {
            return NotificationEntity.NotificationReadStatus.valueOf(readStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}