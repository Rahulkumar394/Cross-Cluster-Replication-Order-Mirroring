package com.example.producerservice.service;

import com.example.producerservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducerService.class);

    @Value("${app.topic.name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    public void sendOrder(Order order) {
        LOGGER.info(String.format("Sending Order -> %s to topic -> %s", order.toString(), topicName));
        // We use orderId as the key to ensure orders with the same ID go to the same partition
        kafkaTemplate.send(topicName, order.getOrderId(), order);
    }
}