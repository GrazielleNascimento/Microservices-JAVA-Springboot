package br.com.msappointment.integrations.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    // queues and routing keys for appointment scheduling
    public static final String APPOINTMENT_EXCHANGE_NAME = "appointmentExchange";
    public static final String APPOINTMENT_STATUS_QUEUE_NAME = "appointment.status";
    public static final String APPOINTMENT_STATUS_ROUTING_KEY = "appointment.status.key";


    // queus and routing keys for pet
    public static final String PET_EXCHANGE_NAME = "petsExchange";
    public static final String PET_CREATED_QUEUE_NAME = "pet.created";
    public static final String PET_INFO_REQUEST_QUEUE_NAME = "pet.info.requests";
    public static final String PET_INFO_REQUEST_ROUTING_KEY = "pet.info.request.key";
    public static final String PET_INFO_RESPONSE_QUEUE_NAME = "pet.info.responses";
    public static final String PET_INFO_RESPONSE_ROUTING_KEY = "pet.info.response.key";


    @Bean
    public TopicExchange appointmentExchange() {
        logger.info("Creating TopicExchange with name: {}", APPOINTMENT_EXCHANGE_NAME);
        return new TopicExchange(APPOINTMENT_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange petExchange() {
        logger.info("Creating TopicExchange with name: {}", PET_EXCHANGE_NAME);
        return new TopicExchange(PET_EXCHANGE_NAME);
    }

    @Bean
    public Queue appointmentQueue() {
        logger.info("Creating Queue with name: {}", APPOINTMENT_STATUS_QUEUE_NAME);
        return new Queue(APPOINTMENT_STATUS_QUEUE_NAME);
    }

    @Bean
    public Queue petCreatedQueue() {
        logger.info("Creating Queue with name: {}", PET_CREATED_QUEUE_NAME);
        return new Queue(PET_CREATED_QUEUE_NAME);
    }

    @Bean
    public Queue petInfoRequestQueue() {
        logger.info("Creating Queue with name: {}", PET_INFO_REQUEST_QUEUE_NAME);
        return new Queue(PET_INFO_REQUEST_QUEUE_NAME);
    }

    @Bean
    public Queue petInfoResponseQueue() {
        logger.info("Creating Queue with name: {}", PET_INFO_RESPONSE_QUEUE_NAME);
        return new Queue(PET_INFO_RESPONSE_QUEUE_NAME);
    }

    @Bean
    public Binding appointmentQueueBinding(@Qualifier("appointmentQueue") Queue appointmentQueue, @Qualifier("appointmentExchange") TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", APPOINTMENT_STATUS_QUEUE_NAME, APPOINTMENT_EXCHANGE_NAME, APPOINTMENT_STATUS_ROUTING_KEY);
        return BindingBuilder.bind(appointmentQueue).to(exchange).with(APPOINTMENT_STATUS_ROUTING_KEY);
    }

    @Bean
    public Binding petCreatedQueueBinding(@Qualifier("petCreatedQueue") Queue petCreatedQueue, @Qualifier("petExchange") TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", PET_CREATED_QUEUE_NAME, PET_EXCHANGE_NAME, PET_CREATED_QUEUE_NAME);
        return BindingBuilder.bind(petCreatedQueue).to(exchange).with(PET_CREATED_QUEUE_NAME);
    }

    @Bean
    public Binding petInfoRequestQueueBinding(@Qualifier("petInfoRequestQueue") Queue petInfoRequestQueue, @Qualifier("petExchange") TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", PET_INFO_REQUEST_QUEUE_NAME, PET_EXCHANGE_NAME, PET_INFO_REQUEST_ROUTING_KEY);
        return BindingBuilder.bind(petInfoRequestQueue).to(exchange).with(PET_INFO_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding petInfoResponseQueueBinding(@Qualifier("petInfoResponseQueue") Queue petInfoResponseQueue, @Qualifier("petExchange") TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", PET_INFO_RESPONSE_QUEUE_NAME, PET_EXCHANGE_NAME, PET_INFO_RESPONSE_ROUTING_KEY);
        return BindingBuilder.bind(petInfoResponseQueue).to(exchange).with(PET_INFO_RESPONSE_ROUTING_KEY);
    }

}