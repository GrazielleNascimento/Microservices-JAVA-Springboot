package br.com.msappointment.integration.rabbitmq.producer;

import br.com.msappointment.integrations.rabbitmq.config.RabbitMQConfig;
import br.com.msappointment.integrations.rabbitmq.producer.RabbitMQProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQProducerTest {

    @Autowired
    RabbitMQProducer rabbitMQProducer;

    @Test
    public void shouldSendMessageToPetIfoRequestQueue() {
        rabbitMQProducer.createMessage(
                RabbitMQConfig.PET_EXCHANGE_NAME,
                RabbitMQConfig.PET_INFO_REQUEST_ROUTING_KEY,
                "Quero o Mor"
        );
    }

}
