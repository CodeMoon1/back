package com.oceanodosdados.consumer;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import com.oceanodosdados.config.RabitMQConfig;

@Component
@RabbitListener(queues = RabitMQConfig.QUEUE_NAME)
public class Consumer {

    @RabbitHandler
    public void consumir(Map<String, Object> payload) {
    System.out.println("Mensagem recebida:");
    System.out.println(payload);
}
}
