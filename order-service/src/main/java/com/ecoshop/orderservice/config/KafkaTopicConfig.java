package com.ecoshop.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders-topic")
                .partitions(1) // Em produção, usaríamos mais partições para paralelismo
                .replicas(1)   // Quantas cópias do dado (como é local, 1 é suficiente)
                .build();
    }
}
