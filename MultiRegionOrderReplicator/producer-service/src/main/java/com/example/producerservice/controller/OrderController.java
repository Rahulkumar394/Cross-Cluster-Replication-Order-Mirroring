package com.example.producerservice.controller;

import com.example.producerservice.model.Order;
import com.example.producerservice.service.OrderProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderProducerService producerService;

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody Order orderRequest) {
        // In a real app, you'd create a new Order object, but for simplicity, we'll use the request body directly.
        // Let's create a new Order object to ensure it has a unique ID and timestamp.
        Order newOrder = new Order(
            orderRequest.getProductId(),
            orderRequest.getQuantity(),
            orderRequest.getPrice(),
            orderRequest.getCustomerId()
        );
        
        producerService.sendOrder(newOrder);
        return ResponseEntity.ok("Order sent to local cluster successfully! Order ID: " + newOrder.getOrderId());
    }
}