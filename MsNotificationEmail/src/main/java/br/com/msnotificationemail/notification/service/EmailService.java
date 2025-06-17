package br.com.msnotificationemail.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${notification.host_name}")
    private String hostName;

    @Value("${notification.port}")
    private String hostPort;

    public void sendMessage(String to, String subject, String message, Boolean isHtml) throws MessagingException {
        logger.info("Enviando e-mail para: {}", to);

        JavaMailSenderImpl mailSender = configureMailSender();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // Especificar UTF-8
        helper.setFrom("contato@clinicapatasdeouro.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, isHtml);

        mailSender.send(mimeMessage);
        logger.info("E-mail enviado com sucesso para: {}", to);
    }

    private JavaMailSenderImpl configureMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(hostName);
        mailSender.setPort(Integer.valueOf(hostPort));
        mailSender.setUsername("user");
        mailSender.setPassword("password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }
}