package br.com.petsgft.integration.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String PET_EXCHANGE_NAME = "petsExchange";
    public static final String PET_CREATED_QUEUE_NAME = "pet.created";
    public static final String PET_INFO_REQUEST_QUEUE_NAME = "pet.info.requests";
    public static final String PET_INFO_RESPONSE_QUEUE_NAME = "pet.info.responses";
    public static final String PET_CREATED_ROUTING_KEY = "pet.created.key";
    public static final String PET_RESPONSE_ROUTING_KEY = "pet.info.response.key";

    @Bean
    public TopicExchange exchange() {
        logger.info("Creating TopicExchange with name: {}", PET_EXCHANGE_NAME);
        return new TopicExchange(PET_EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        logger.info("Creating Queue with name: {}", PET_CREATED_QUEUE_NAME);
        return new Queue(PET_CREATED_QUEUE_NAME);
    }

    @Bean
    public Queue requestQueue() {
        logger.info("Creating Queue with name: {}", PET_INFO_REQUEST_QUEUE_NAME);
        return new Queue(PET_INFO_REQUEST_QUEUE_NAME);
    }

    @Bean
    public Queue responseQueue() {
        logger.info("Creating Queue with name: {}", PET_INFO_RESPONSE_QUEUE_NAME);
        return new Queue(PET_INFO_RESPONSE_QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", PET_CREATED_QUEUE_NAME, PET_EXCHANGE_NAME, PET_CREATED_ROUTING_KEY);
        return BindingBuilder.bind(queue).to(exchange).with(PET_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding requestBinding(Queue requestQueue, TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", PET_INFO_REQUEST_QUEUE_NAME, PET_EXCHANGE_NAME, PET_INFO_REQUEST_QUEUE_NAME);
        return BindingBuilder.bind(requestQueue).to(exchange).with(PET_INFO_REQUEST_QUEUE_NAME);
    }

    @Bean
    public Binding responseBinding(Queue responseQueue, TopicExchange exchange) {
        logger.info("Binding Queue: {} to Exchange: {} with Routing Key: {}", PET_INFO_RESPONSE_QUEUE_NAME, PET_EXCHANGE_NAME, PET_RESPONSE_ROUTING_KEY);
        return BindingBuilder.bind(responseQueue).to(exchange).with(PET_RESPONSE_ROUTING_KEY);
    }

}
