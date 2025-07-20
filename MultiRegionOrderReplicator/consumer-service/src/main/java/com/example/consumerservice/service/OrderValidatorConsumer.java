package com.example.consumerservice.service;

import com.example.consumerservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderValidatorConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderValidatorConsumer.class);

    // This listener consumes from the 'orders' topic on the REMOTE cluster
    @KafkaListener(topics = "${app.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAndValidate(Order order) {
        LOGGER.info(String.format("<<<<< Received replicated order: %s", order.toString()));

        // Data Validation Logic
        boolean isValid = true;
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            LOGGER.error("Validation FAILED: Order ID is missing.");
            isValid = false;
        }
        if (order.getProductId() == null || order.getProductId().isEmpty()) {
            LOGGER.error("Validation FAILED: Product ID is missing.");
            isValid = false;
        }
        if (order.getQuantity() <= 0) {
            LOGGER.error("Validation FAILED: Quantity must be positive.");
            isValid = false;
        }
        if (order.getPrice() <= 0.0) {
            LOGGER.error("Validation FAILED: Price must be positive.");
            isValid = false;
        }

        if (isValid) {
            LOGGER.info(String.format(">>>>> Order VALIDATED SUCCESSFULLY: %s", order.getOrderId()));
            // Yahan par aap order ko database mein save kar sakte hain ya aage process kar sakte hain.
        } else {
            LOGGER.warn(String.format("Order %s failed validation. Moving to a dead-letter queue or logging for manual review.", order.getOrderId()));
            // Production system mein, invalid messages ko Dead-Letter Queue (DLQ) mein bhejte hain.
        }
    }
}