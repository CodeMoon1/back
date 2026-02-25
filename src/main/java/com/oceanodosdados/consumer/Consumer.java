package com.oceanodosdados.consumer;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import com.oceanodosdados.config.RabitMQConfig;

@Component
@RabbitListener(queues = RabitMQConfig.QUEUE_NAME)
public class Consumer {

    @RabbitHandler
    public void receiveMessage(String message) {
        System.out.println("=========================================================");
        System.out.println("Received Message: " + message);
    }
}
