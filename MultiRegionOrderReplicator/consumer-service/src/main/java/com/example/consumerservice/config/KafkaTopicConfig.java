package com.example.consumerservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // Yeh 'app.topic.name' ko application.properties se uthayega
    @Value("${app.topic.name}")
    private String topicName;

    // Yeh bean consumer application ke start hote hi 'remote' cluster par topic bana dega
    @Bean
    public NewTopic ordersTopicOnRemote() {
        return TopicBuilder.name(topicName)
                .partitions(3) // Producer ke topic jaisa hi partition count
                .replicas(1)   // Hamare remote cluster mein 1 hi broker hai
                .build();
    }
}