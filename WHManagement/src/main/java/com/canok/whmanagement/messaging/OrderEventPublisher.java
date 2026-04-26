package com.canok.whmanagement.messaging;

import com.canok.whmanagement.config.RabbitMQConfig;
import com.canok.whmanagement.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public void publishOrderCreated(Order order) {
        amqpTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, order);
    }

    public void publishOrderStatusUpdated(Order order) {
        amqpTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, order);
    }
}
