package br.com.msnotificationemail.notification.repository;

import br.com.msnotificationemail.notification.entity.NotificationEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotificationSpecification {

    public static Specification<NotificationEntity> withPetId(Integer petId) {
        return (root, query, criteriaBuilder) ->
                petId == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("petId"), petId);
    }

    public static Specification<NotificationEntity> withPetName(String petName) {
        return (root, query, criteriaBuilder) ->
                petName == null || petName.isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("petName")),
                        "%" + petName.toLowerCase() + "%");
    }

    public static Specification<NotificationEntity> withTutorId(Integer tutorId) {
        return (root, query, criteriaBuilder) ->
                tutorId == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("tutorId"), tutorId);
    }

    public static Specification<NotificationEntity> withTutorName(String tutorName) {
        return (root, query, criteriaBuilder) ->
                tutorName == null || tutorName.isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tutorName")),
                        "%" + tutorName.toLowerCase() + "%");
    }

    public static Specification<NotificationEntity> withTutorEmail(String tutorEmail) {
        return (root, query, criteriaBuilder) ->
                tutorEmail == null || tutorEmail.isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tutorEmail")),
                        "%" + tutorEmail.toLowerCase() + "%");
    }

    public static Specification<NotificationEntity> withStatus(NotificationEntity.NotificationStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<NotificationEntity> withNotificationType(String notificationType) {
        return (root, query, criteriaBuilder) ->
                notificationType == null || notificationType.isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("notificationType"), notificationType);
    }

    public static Specification<NotificationEntity> withReadStatus(NotificationEntity.NotificationReadStatus readStatus) {
        return (root, query, criteriaBuilder) ->
                readStatus == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("readStatus"), readStatus);
    }

    public static Specification<NotificationEntity> withDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            } else if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("sentAt"), endDate);
            } else if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("sentAt"), startDate);
            } else {
                return criteriaBuilder.between(root.get("sentAt"), startDate, endDate);
            }
        };
    }

    public static Specification<NotificationEntity> withSentAt(LocalDateTime sentAt) {
        return (root, query, criteriaBuilder) ->
                sentAt == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("sentAt"), sentAt);
    }

    public static Specification<NotificationEntity> withReadAt(LocalDateTime readAt) {
        return (root, query, criteriaBuilder) ->
                readAt == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("readAt"), readAt);
    }

}