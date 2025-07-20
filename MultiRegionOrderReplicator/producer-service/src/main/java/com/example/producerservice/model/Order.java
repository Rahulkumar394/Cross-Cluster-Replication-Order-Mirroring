package com.example.producerservice.model;

import java.time.Instant;
import java.util.UUID;

public class Order {
    private String orderId;
    private String productId;
    private int quantity;
    private double price;
    private String customerId;
    private Instant orderTimestamp;

    // Default constructor for JSON deserialization
    public Order() {
    }

    public Order(String productId, int quantity, double price, String customerId) {
        this.orderId = UUID.randomUUID().toString();
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.customerId = customerId;
        this.orderTimestamp = Instant.now();
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Instant getOrderTimestamp() { return orderTimestamp; }
    public void setOrderTimestamp(Instant orderTimestamp) { this.orderTimestamp = orderTimestamp; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", customerId='" + customerId + '\'' +
                ", orderTimestamp=" + orderTimestamp +
                '}';
    }
}