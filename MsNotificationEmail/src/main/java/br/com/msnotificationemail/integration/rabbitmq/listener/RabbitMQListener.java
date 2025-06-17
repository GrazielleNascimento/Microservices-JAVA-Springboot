package br.com.msnotificationemail.integration.rabbitmq.listener;

import br.com.msnotificationemail.integration.rabbitmq.config.RabbitMQConfig;
import br.com.msnotificationemail.notification.dto.AppointmentDTO;
import br.com.msnotificationemail.notification.entity.NotificationEntity;
import br.com.msnotificationemail.notification.repository.NotificationRepository;
import br.com.msnotificationemail.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class RabbitMQListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);

    private final NotificationService notificationService;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;

    @Autowired
    public RabbitMQListener(
            NotificationService notificationService,
            RetryTemplate retryTemplate,
            ObjectMapper objectMapper,
            NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.retryTemplate = retryTemplate;
        this.objectMapper = objectMapper;
        this.notificationRepository = notificationRepository;
    }

 @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_STATUS_QUEUE_NAME)
    public void receiveMessage(Message message) {
        try {
            // Converte a mensagem bruta para String
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            logger.info("Mensagem recebida: {}", messageBody);

            // Converte a String JSON para o objeto AppointmentDTO
            AppointmentDTO appointmentDTO = objectMapper.readValue(messageBody, AppointmentDTO.class);

            // Log detalhado para debug
            logger.info("Dados do agendamento: ID={}, Pet={}, Data={}, TutorName={}, TutorEmail={}, Descrição={}",
                    appointmentDTO.getId(),
                    appointmentDTO.getPetName(),
                    appointmentDTO.getAppointmentDate(),
                    appointmentDTO.getTutorName(),
                    appointmentDTO.getTutorEmail(),
                    appointmentDTO.getDescription());

            // Validação de campos obrigatórios
            validateAppointmentData(appointmentDTO);
            boolean notificationSent = retryTemplate.execute(context -> {
                try {
                    notificationService.sendNotification(appointmentDTO);
                    logger.info("Notificação enviada com sucesso para: {}", appointmentDTO.getTutorEmail());
                    return true; // Indica sucesso
                } catch (MessagingException e) {
                    logger.warn("Tentativa {} falhou: {}", context.getRetryCount() + 1, e.getMessage());
                    throw new RuntimeException("Erro no processamento: " + e.getMessage(), e);
                }
            }, recoveryContext -> {
                logger.error("Todas as tentativas falharam para o agendamento {}", appointmentDTO.getId());
                // Atualizar o registro para falha permanente
                updateNotificationToFailed(appointmentDTO, "Falha após múltiplas tentativas");
                return false; // Indica falha
            });

            if (notificationSent) {
                logger.info("A notificação foi enviada com sucesso.");
            } else {
                logger.error("Falha ao enviar a notificação após múltiplas tentativas.");
            }

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar mensagem: " + e.getMessage(), e);
        }
    }

    private void validateAppointmentData(AppointmentDTO appointmentDTO) {
        if (appointmentDTO.getId() == null) {
            logger.warn("ID do agendamento não informado, usando valor padrão");
            appointmentDTO.setId(0);
        }

        if (appointmentDTO.getPetId() == null) {
            logger.warn("ID do pet não informado, usando valor padrão");
            appointmentDTO.setPetId(0);
        }

        if (appointmentDTO.getPetName() == null || appointmentDTO.getPetName().isEmpty()) {
            logger.warn("Nome do pet não informado, usando valor padrão");
            appointmentDTO.setPetName("seu pet");
        }

        if (appointmentDTO.getAppointmentDate() == null) {
            logger.warn("Data do agendamento não informada, usando valor padrão");
            appointmentDTO.setAppointmentDate("a confirmar");
        }

        if (appointmentDTO.getDescription() == null || appointmentDTO.getDescription().isEmpty()) {
            logger.warn("Descrição não informada, usando valor padrão");
            appointmentDTO.setDescription("Consulta regular");
        }

        if (appointmentDTO.getTutorId() == null) {
            logger.warn("ID do tutor não informado, usando valor padrão");
            appointmentDTO.setTutorId(0);
        }

        if (appointmentDTO.getTutorName() == null || appointmentDTO.getTutorName().isEmpty()) {
            logger.warn("Nome do tutor não informado, usando valor padrão");
            appointmentDTO.setTutorName("Tutor");
        }

        if (appointmentDTO.getTutorEmail() == null || appointmentDTO.getTutorEmail().isEmpty()) {
            logger.warn("Email do tutor não informado, usando valor padrão");
            appointmentDTO.setTutorEmail("sem-email@example.com");
        }
    }


    private void updateNotificationToFailed(AppointmentDTO appointmentDTO, String errorMessage) {
        try {
            // Buscar por notificações pendentes para este agendamento
            notificationRepository.findAll().stream()
                    .filter(n -> n.getAppointmentId().equals(appointmentDTO.getId()) &&
                            n.getStatus() == NotificationEntity.NotificationStatus.PENDING)
                    .forEach(notification -> {
                        notification.setStatus(NotificationEntity.NotificationStatus.FAILED);
                        notification.setErrorMessage(errorMessage);
                        notificationRepository.save(notification);
                    });
        } catch (Exception e) {
            logger.warn("Erro ao atualizar status da notificação para falha: {}", e.getMessage());
        }
    }
}