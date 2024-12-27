package com.bitcamp.drrate.global.config;

import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;


// Kafka 토픽 생성에 필요한 설정을 추가
@Configuration
public class KafkaConfig {
    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                "bootstrap.servers", "localhost:9092"
        ));
    }
    @Bean
    public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
        // KafkaAdmin의 설정을 가져와 AdminClient 생성
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }
}
