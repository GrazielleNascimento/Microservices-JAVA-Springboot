package br.com.msnotificationemail.notification.service;

import br.com.msnotificationemail.notification.dto.AppointmentDTO;
import br.com.msnotificationemail.notification.entity.NotificationEntity;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    NotificationService notificationService;

    @Test
    public void test() {
        //TESTA PARA SALVAR NO BANCO
        AppointmentDTO appointmentDTO = AppointmentDTO.builder()
                .id(1)
                .petId(4)
                .petName("Rayka")
                .appointmentDate(LocalDateTime.now().toString())
                .tutorId(1)
                .tutorName("Esmeralda")
                .tutorEmail("esmeralda@gmail.com")
                .description("Consulta de rotina")
                .build();
        String subject = "Consulta agendada";
        String content = "Olá, Esmeralda! Sua consulta com Rayka foi agendada para " + appointmentDTO.getAppointmentDate();
        String errorMessage = null;

        notificationService.saveNotificationRecord(
                appointmentDTO,
                subject,
                content,
                NotificationEntity.NotificationStatus.SENT,
                errorMessage
        );

    }

    @Test
    public void shouldSendNotificationWithSucess () throws MessagingException {
        //TESTA PARA ENVIAR O EMAIL NO SERVIDOR DE EMAIL
        AppointmentDTO appointmentDTO = AppointmentDTO.builder()
                .id(1)
                .petId(4)
                .petName("Rayka")
                .appointmentDate(LocalDateTime.now().toString())
                .tutorId(1)
                .tutorName("Esmeralda")
                .tutorEmail("esmeralda@gmail.com")
                .description("Consulta de rotina")
                .build();

        notificationService.sendNotification(appointmentDTO);

    }

}
