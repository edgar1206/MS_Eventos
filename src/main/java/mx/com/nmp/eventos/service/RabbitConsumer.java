package mx.com.nmp.eventos.service;

import mx.com.nmp.eventos.model.constant.Constants;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class RabbitConsumer {

    @Autowired
    private AmqpTemplate rabbiTemplate;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void recibeLog(Message message){

        System.out.println("recibe");

    }

}
