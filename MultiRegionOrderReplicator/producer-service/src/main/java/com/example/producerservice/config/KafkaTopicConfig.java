package com.example.producerservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.topic.name}")
    private String topicName;

    @Bean
    public NewTopic ordersTopic() {
        // This will create the 'orders' topic on the local cluster when the app starts
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1) // In a single-broker cluster, replica must be 1
                .build();
    }
}