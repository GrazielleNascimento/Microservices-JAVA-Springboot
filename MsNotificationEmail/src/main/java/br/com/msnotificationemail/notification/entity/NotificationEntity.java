package br.com.msnotificationemail.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer appointmentId;

    @Column(nullable = false)
    private Integer petId;

    @Column(nullable = false)
    private String petName;

    @Column(nullable = false)
    private Integer tutorId;

    @Column(nullable = false)
    private String tutorName;

    @Column(nullable = false)
    private String tutorEmail;

    @Column(columnDefinition = "TEXT")
    private String emailSubject;

    @Column(columnDefinition = "TEXT")
    private String emailContent;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private NotificationReadStatus readStatus;

    @Column
    private String errorMessage;

    @Column
    private String notificationType;

    public enum NotificationStatus {
        SENT, PENDING, FAILED
    }

    public enum NotificationReadStatus {
        READ, UNREAD
    }

    @Column(nullable = true)
    private LocalDateTime readAt;

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