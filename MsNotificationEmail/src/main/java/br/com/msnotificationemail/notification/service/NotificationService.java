package br.com.msnotificationemail.notification.service;

import br.com.msnotificationemail.notification.dto.AppointmentDTO;
import br.com.msnotificationemail.notification.dto.NotificationFilterDTO;
import br.com.msnotificationemail.notification.dto.NotificationDTO;
import br.com.msnotificationemail.notification.entity.AppointmentStatusEnum;
import br.com.msnotificationemail.notification.entity.NotificationEntity;
import br.com.msnotificationemail.notification.repository.NotificationRepository;
import br.com.msnotificationemail.notification.repository.NotificationSpecification;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @Value("${app.tracking.url}")
    private String trackingUrl;

    @Autowired
    public NotificationService(EmailService emailService, NotificationRepository notificationRepository) {
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(AppointmentDTO appointmentDTO) throws MessagingException {
        logger.info("Preparando notificação para pet: {}", appointmentDTO.getPetName());

        String emailTutor = appointmentDTO.getTutorEmail();
       // String emailSubject = "Confirmação de Agendamento - Clínica Patas de Ouro ";
        String emailSubject = getStatusEmailSubject(appointmentDTO.getStatus());

        // Primeiro, salvar o registro para obter o ID da notificação
        NotificationEntity notificationEntity = saveNotificationRecord(
                appointmentDTO,
                emailSubject,
                "", // Conteúdo será atualizado depois
                NotificationEntity.NotificationStatus.PENDING, // Status temporário
                null
        );

        // Criar email com emojis para enviar
        String emailMessage = formatEmailMessage(appointmentDTO, notificationEntity.getId());

        // Criar versão sem emojis para salvar no banco
        String emailMessageSemEmojis = removeEmojis(emailMessage);

        try {
            // Atualizar o conteúdo do email e o status
            notificationEntity.setEmailContent(emailMessageSemEmojis);
            notificationEntity.setStatus(NotificationEntity.NotificationStatus.SENT);
            notificationRepository.save(notificationEntity);

            // Envia email com emojis
            emailService.sendMessage(emailTutor, emailSubject, emailMessage, true);

            logger.info("Notificação enviada com sucesso para: {}", emailTutor);
        } catch (MessagingException e) {
            logger.error("Erro ao enviar notificação para: {}", emailTutor, e);

            // Atualizar notificação existente para FAILED
            notificationEntity.setStatus(NotificationEntity.NotificationStatus.FAILED);
            notificationEntity.setErrorMessage(e.getMessage());
            notificationRepository.save(notificationEntity);

            throw e;
        }
    }

    private String removeEmojis(String text) {
        return text.replaceAll("[\\p{So}\\p{Cn}]", "");
    }

    public NotificationEntity saveNotificationRecord(
            AppointmentDTO appointmentDTO,
            String subject,
            String content,
            NotificationEntity.NotificationStatus status,
            String errorMessage) {

        NotificationEntity notification = NotificationEntity.builder()
                .appointmentId(appointmentDTO.getId())
                .petId(appointmentDTO.getPetId() != null ? appointmentDTO.getPetId() : 0)
                .petName(appointmentDTO.getPetName())
                .tutorId(appointmentDTO.getTutorId() != null ? appointmentDTO.getTutorId() : 0)
                .tutorName(appointmentDTO.getTutorName() != null ? appointmentDTO.getTutorName() : "Tutor")
                .tutorEmail(appointmentDTO.getTutorEmail())
                .emailSubject(subject)
                .emailContent(content)
                .sentAt(LocalDateTime.now())
                .status(status)
                .readStatus(NotificationEntity.NotificationReadStatus.UNREAD)
                .errorMessage(errorMessage)
                .notificationType("AGENDAMENTO")
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsWithFilters(
            NotificationFilterDTO filterDTO,
            Pageable pageable) {
        logger.info("### INICIANDO BUSCA COM FILTROS ### Filtros recebidos: {}", filterDTO);
        logger.info("### PAGINAÇÃO ### page: {}, size: {}, sort: {}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Specification<NotificationEntity> spec = Specification
                .where(NotificationSpecification.withPetId(filterDTO.getPetId()))
                .and(NotificationSpecification.withPetName(filterDTO.getPetName()))
                .and(NotificationSpecification.withTutorId(filterDTO.getTutorId()))
                .and(NotificationSpecification.withTutorName(filterDTO.getTutorName()))
                .and(NotificationSpecification.withTutorEmail(filterDTO.getTutorEmail()))
                .and(NotificationSpecification.withStatus(filterDTO.getStatusEnum()))
                .and(NotificationSpecification.withReadStatus(filterDTO.getReadStatusEnum()))
                .and(NotificationSpecification.withNotificationType(filterDTO.getNotificationType()))
                .and(NotificationSpecification.withDateRange(filterDTO.getStartDate(), filterDTO.getEndDate()))
                .and(NotificationSpecification.withSentAt(filterDTO.getSentAt()))
                .and(NotificationSpecification.withReadAt(filterDTO.getReadAt()));

        logger.debug("### EXECUTANDO QUERY COM SPECIFICATIONS ###");
        Page<NotificationEntity> notifications = notificationRepository.findAll(spec, pageable);

        logger.info("### RESULTADO DA BUSCA ### Total de elementos: {}, Total de páginas: {}",
                notifications.getTotalElements(),
                notifications.getTotalPages());

        return notifications.map(NotificationDTO::fromEntity);
    }


    @Transactional
    public NotificationDTO markAsRead(Integer notificationId) {
        logger.info("### INICIANDO MARCAÇÃO DE LEITURA ### Tentando marcar notificação ID: {} como lida", notificationId);

        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);

        if (!notificationOpt.isPresent()) {
            logger.warn("### FALHA ### Notificação com ID: {} não encontrada", notificationId);
            return null;
        }

        NotificationEntity notification = notificationOpt.get();
        logger.info("### NOTIFICAÇÃO ENCONTRADA ### ID: {}, Status Atual: {}, ReadAt Atual: {}",
                notificationId, notification.getReadStatus(), notification.getReadAt());

        // Definir o status de leitura e timestamp
        notification.setReadStatus(NotificationEntity.NotificationReadStatus.READ);
        notification.setReadAt(LocalDateTime.now());

        logger.info("### ATUALIZANDO ### Definindo readStatus={} e readAt={}",
                NotificationEntity.NotificationReadStatus.READ, notification.getReadAt());

        // Salvar e obter a entidade atualizada
        NotificationEntity savedNotification = notificationRepository.save(notification);

        logger.info("### SALVAMENTO CONCLUÍDO ### Notificação ID: {} salva. readStatus={}, readAt={}",
                notificationId, savedNotification.getReadStatus(), savedNotification.getReadAt());

        // Forçar o flush do EntityManager para garantir que as alterações sejam persistidas imediatamente
        notificationRepository.flush();

        return NotificationDTO.fromEntity(savedNotification);
    }

    @Transactional
    public List<NotificationDTO> markMultipleAsRead(List<Integer> notificationIds) {
        List<NotificationEntity> notifications = notificationRepository.findAllById(notificationIds);

        notifications.forEach(notification -> notification.setReadStatus(NotificationEntity.NotificationReadStatus.READ));

        return notificationRepository.saveAll(notifications).stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO resendNotification(Integer notificationId) throws MessagingException {
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isPresent()) {
            NotificationEntity notification = notificationOpt.get();

            if (notification.getStatus() == NotificationEntity.NotificationStatus.FAILED) {
                try {
                    emailService.sendMessage(
                            notification.getTutorEmail(),
                            notification.getEmailSubject(),
                            notification.getEmailContent(),
                            true
                    );

                    notification.setStatus(NotificationEntity.NotificationStatus.SENT);
                    notification.setErrorMessage(null);
                    notification.setSentAt(LocalDateTime.now());

                    return NotificationDTO.fromEntity(notificationRepository.save(notification));
                } catch (MessagingException e) {
                    notification.setErrorMessage(e.getMessage());
                    notification.setSentAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    throw e;
                }
            }

            return NotificationDTO.fromEntity(notification);
        }

        return null;
    }

    private String getStatusEmailSubject(AppointmentStatusEnum status) {
        switch (status) {
            case PENDING:
                return "Agendamento Pendente - Clínica Patas de Ouro";
            case CONFIRMED:
                return "Agendamento Confirmado - Clínica Patas de Ouro";
            case RESCHEDULED:
                return "Agendamento Remarcado - Clínica Patas de Ouro";
            case CANCELLED:
                return "Agendamento Cancelado - Clínica Patas de Ouro";
            default:
                return "Atualização de Agendamento - Clínica Patas de Ouro";
        }
    }

    private String getStatusHeaderText(AppointmentStatusEnum status) {
        switch (status) {
            case PENDING:
                return "Agendamento Pendente <span class='emoji'>📝🐾</span>";
            case CONFIRMED:
                return "Agendamento Confirmado <span class='emoji'>✅🐾</span>";
            case RESCHEDULED:
                return "Agendamento Remarcado <span class='emoji'>🔄🐾</span>";
            case CANCELLED:
                return "Agendamento Cancelado <span class='emoji'>❌🐾</span>";
            default:
                return "Atualização de Agendamento <span class='emoji'>ℹ️🐾</span>";
        }
    }

    private String getStatusSpecificMessage(AppointmentStatusEnum status, String petName) {
        switch (status) {
            case PENDING:
                return String.format("<p>Recebemos sua solicitação de agendamento para %s na Clínica Patas de Ouro. " +
                        "Nossa equipe irá analisar a disponibilidade e entraremos em contato em breve para confirmar. " +
                        "<span class='emoji'>📝✨</span></p>" +
                        "<p>Por favor, aguarde nosso contato por telefone ou e-mail para confirmarmos seu horário.</p>", petName);
            case CONFIRMED:
                return String.format("<p>O agendamento para %s foi confirmado! " +
                        "Estamos ansiosos para recebê-los em nossa clínica. <span class='emoji'>✅💖</span></p>" +
                        "<p>Lembretes importantes:</p>" +
                        "<ul>" +
                        "<li>Chegue 10 minutos antes do horário marcado</li>" +
                        "<li>Traga o histórico médico do pet (se houver)</li>" +
                        "<li>Se houver exames agendados, mantenha o pet em jejum</li>" +
                        "</ul>", petName);
            case RESCHEDULED:
                return String.format("<p>O agendamento para %s foi remarcado conforme solicitado. " +
                        "<span class='emoji'>🔄✨</span></p>" +
                        "<p>Se o novo horário não for adequado, entre em contato conosco " +
                        "o mais breve possível para buscarmos uma alternativa.</p>" +
                        "<p>Pedimos desculpas por qualquer inconveniente.</p>", petName);
            case CANCELLED:
                return String.format("<p>O agendamento para %s foi cancelado conforme solicitado. " +
                        "<span class='emoji'>❌💭</span></p>" +
                        "<p>Esperamos poder atendê-los em uma próxima oportunidade. " +
                        "Quando desejar realizar um novo agendamento, estaremos à disposição.</p>" +
                        "<p>Agradecemos a compreensão!</p>", petName);
            default:
                return String.format("<p>Houve uma atualização no agendamento de %s. " +
                        "Entre em contato conosco para mais informações.</p>", petName);
        }
    }

    private String formatAppointmentDateTime(String dateTimeStr) {
        if (dateTimeStr == null) {
            return "A confirmar";
        }

        try {
            if (dateTimeStr.contains("T")) {
                String[] parts = dateTimeStr.split("T");
                if (parts.length == 2) {
                    String[] datePieces = parts[0].split("-");
                    String[] timePieces = parts[1].split(":");

                    if (datePieces.length == 3 && timePieces.length >= 2) {
                        String minute = timePieces[1];
                        if (minute.contains(".")) {
                            minute = minute.substring(0, minute.indexOf("."));
                        }

                        return String.format("%s/%s/%s às %s:%s",
                                datePieces[2], datePieces[1], datePieces[0],
                                timePieces[0], minute);
                    }
                }
            }
            return dateTimeStr;
        } catch (Exception e) {
            logger.warn("Erro ao formatar data: {}", e.getMessage());
            return dateTimeStr;
        }
    }

    private String formatEmailMessage(AppointmentDTO appointmentDTO, Integer notificationId) {
        String greeting = appointmentDTO.getTutorName() != null && !appointmentDTO.getTutorName().isEmpty()
                ? "Olá, " + appointmentDTO.getTutorName()
                : "Olá";

        String procedimento = appointmentDTO.getDescription() != null ? appointmentDTO.getDescription() : "Consulta regular";
        String dataHora = formatAppointmentDateTime(appointmentDTO.getAppointmentDate());
        String codigoAgendamento = appointmentDTO.getId() != null ? appointmentDTO.getId().toString() : "N/A";
        String statusMessage = getStatusSpecificMessage(appointmentDTO.getStatus(), appointmentDTO.getPetName());
        String headerText = getStatusHeaderText(appointmentDTO.getStatus());
        String confirmationButton =
                "<a href='" + trackingUrl + "/api/notifications/tracking/" + notificationId +
                        "' style='display:block; background-color:#d4af37; color:white; padding:10px; text-align:center; text-decoration:none; border-radius:5px; margin-top:20px;'>" +
                        "Confirmar leitura deste email" +
                        "</a>";

        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "  <meta charset=\"UTF-8\">" +
                        "  <style>" +
                        "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; background-color: #f9f9f9; }" +
                        "    .container { padding: 20px; border: 1px solid #eaeaea; border-radius: 5px; background-color: #fff; }" +
                        "    .header { color: #d4af37; border-bottom: 2px solid #d4af37; padding-bottom: 10px; }" +
                        "    .content { padding: 20px 0; }" +
                        "    .info-box { background-color: #f5f5f5; padding: 15px; border-left: 4px solid #d4af37; margin: 15px 0; }" +
                        "    .footer { margin-top: 30px; font-size: 12px; color: #777; border-top: 1px solid #eaeaea; padding-top: 10px; }" +
                        "    .emoji { font-size: 1.2em; }" +
                        "  </style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "  <h2 class='header'>%s</h2>" +
                        "  <div class='content'>" +
                        "    <p>%s!</p>" +
                        "    %s" + // Status specific message
                        "    <div class='info-box'>" +
                        "      <p><strong>Procedimento:</strong> %s</p>" +
                        "      <p><strong>Data e Hora:</strong> %s</p>" +
                        "      <p><strong>Código do Agendamento:</strong> %s</p>" +
                        "    </div>" +
                        "    %s" + // Confirmation button
                        " </div>" +
                        "  <p>Atenciosamente,<br>Equipe Patas de Ouro <span class='emoji'>✨🐾💛</span></p>" +
                        "  <div class='footer'>" +
                        "    <p>Esta é uma mensagem automática. Por favor, não responda a este e-mail.</p>" +
                        "    <p>Em caso de dúvidas, entre em contato pelo telefone (11) 5555-5555 ou pelo email contato@clinicapatasdeouro.com</p>" +
                        "  </div>" +
                        "</div>" +
                        " <img src='" + trackingUrl + "/api/notifications/tracking/" + notificationId + "' width='1' height='1' style='display:none;'>" +
                        "</body>" +
                        "</html>",
                headerText,
                greeting,
                statusMessage,
                procedimento,
                dataHora,
                codigoAgendamento,
                confirmationButton
        );
    }
}