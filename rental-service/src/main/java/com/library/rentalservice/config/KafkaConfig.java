package com.library.rentalservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic removeQuantity() {
        return TopicBuilder
                .name("removeQuantity")
                .build();
    }

    @Bean
    public NewTopic addQuantity() {
        return TopicBuilder
                .name("addQuantity")
                .build();
    }
}
