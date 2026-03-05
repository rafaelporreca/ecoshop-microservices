package com.ecoshop.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

    // Cria os tópicos caso não existam
    @Bean
    public NewTopic paymentSuccessTopic() {
        return TopicBuilder.name("payment-success-topic").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name("payment-failed-topic").partitions(1).replicas(1).build();
    }

    // O conversor mágico de String para JSON
    @Bean
    public RecordMessageConverter converter() {
        return new StringJsonMessageConverter();
    }
}
