package br.com.msnotificationemail.notification.repository;

import br.com.msnotificationemail.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer>, JpaSpecificationExecutor<NotificationEntity> {

    List<NotificationEntity> findByTutorIdOrderBySentAtDesc(Integer tutorId);

    List<NotificationEntity> findByTutorNameOrderBySentAtDesc(String tutorName);

    List<NotificationEntity> findByTutorEmailOrderBySentAtDesc(String tutorEmail);

    List<NotificationEntity> findByPetNameContainingIgnoreCaseOrderBySentAtDesc(String petName);

    List<NotificationEntity> findByStatus(NotificationEntity.NotificationStatus status);

    List<NotificationEntity> findByNotificationType(String notificationType);

}
