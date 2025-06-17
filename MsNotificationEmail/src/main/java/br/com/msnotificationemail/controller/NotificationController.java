package br.com.msnotificationemail.controller;

import br.com.msnotificationemail.notification.dto.NotificationDTO;
import br.com.msnotificationemail.notification.dto.NotificationFilterDTO;
import br.com.msnotificationemail.notification.entity.NotificationEntity;
import br.com.msnotificationemail.notification.repository.NotificationRepository;
import br.com.msnotificationemail.notification.service.NotificationService;
import br.com.msnotificationemail.util.Response;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationService notificationService, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<Response<List<NotificationDTO>>> getAllNotifications() {
        logger.info("Buscando todas as notificações");

        Response<List<NotificationDTO>> response = new Response<>();
        List<NotificationDTO> notifications = notificationService.getAllNotifications();

        if (notifications.isEmpty()) {
            response.setErrors("Nenhuma notificação encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(notifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Response<Page<NotificationDTO>>> getNotificationsWithFilters(
            @RequestParam(required = false) Integer petId,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) Integer tutorId,
            @RequestParam(required = false) String tutorName,
            @RequestParam(required = false) String tutorEmail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String readStatus,
            @RequestParam(required = false) String notificationType,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) LocalDateTime sentAt,
            @RequestParam(required = false) LocalDateTime readAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sentAt") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        logger.info("Buscando notificações com filtros");

        Response<Page<NotificationDTO>> response = new Response<>();

        NotificationFilterDTO filterDTO = NotificationFilterDTO.builder()
                .petId(petId)
                .petName(petName)
                .tutorId(tutorId)
                .tutorName(tutorName)
                .tutorEmail(tutorEmail)
                .status(status)
                .readStatus(readStatus)
                .notificationType(notificationType)
                .startDate(startDate)
                .endDate(endDate)
                .sentAt(sentAt)
                .readAt(readAt)
                .build();

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<NotificationDTO> notifications = notificationService.getNotificationsWithFilters(filterDTO, pageable);
        response.setData(notifications);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Response<NotificationDTO>> markAsRead(@PathVariable("id") Integer id) {
        logger.info("Marcando notificação {} como lida", id);

        Response<NotificationDTO> response = new Response<>();
        NotificationDTO notification = notificationService.markAsRead(id);

        if (notification == null) {
            response.setErrors("Notificação não encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(notification);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read-batch")
    public ResponseEntity<Response<List<NotificationDTO>>> markMultipleAsRead(@RequestBody List<Integer> notificationIds) {
        logger.info("Marcando múltiplas notificações como lidas: {}", notificationIds);

        Response<List<NotificationDTO>> response = new Response<>();

        if (notificationIds == null || notificationIds.isEmpty()) {
            response.setErrors("Lista de IDs de notificações não pode ser vazia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<NotificationDTO> notificationDTOList = notificationService.markMultipleAsRead(notificationIds);
        response.setData(notificationDTOList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tracking/{id}")
    public ResponseEntity<byte[]> trackEmailOpen(@PathVariable("id") Integer id) {
        logger.info("Email lido: ID {}", id);

        // Obter a notificação antes da atualização
        Optional<NotificationEntity> beforeUpdate = notificationRepository.findById(id);
        logger.info("Status antes da atualização - ID {}: readStatus={}, readAt={}",
                id,
                beforeUpdate.isPresent() ? beforeUpdate.get().getReadStatus() : "N/A",
                beforeUpdate.isPresent() ? beforeUpdate.get().getReadAt() : "N/A");

        // Marcar como lido
        NotificationDTO updatedNotification = notificationService.markAsRead(id);

        // Log detalhado após a atualização
        if (updatedNotification != null) {
            logger.info("Notificação atualizada com sucesso - ID {}: readStatus={}, readAt={}",
                    id, updatedNotification.getReadStatus(), updatedNotification.getReadAt());
        } else {
            logger.warn("Falha ao atualizar notificação - ID {}: notificação não encontrada", id);
        }

        // Retorna um pixel transparente GIF 1x1
        byte[] pixelGif = new byte[]{
                0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, (byte) 0x80, 0x00,
                0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x21, (byte) 0xF9, 0x04,
                0x01, 0x00, 0x00, 0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00,
                0x00, 0x02, 0x01, 0x44, 0x00, 0x3B
        };

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_GIF)
                .body(pixelGif);
    }

    @PostMapping("/{id}/resend")
    public ResponseEntity<Response<NotificationDTO>> resendNotification(@PathVariable Integer id) {
        logger.info("Reenviando notificação: {}", id);

        Response<NotificationDTO> response = new Response<>();

        try {
            NotificationDTO notification = notificationService.resendNotification(id);

            if (notification == null) {
                response.setErrors("Notificação não encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.setData(notification);
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            logger.error("Erro ao reenviar notificação: {}", e.getMessage());
            response.setErrors("Falha ao reenviar notificação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}