package com.bitcamp.drrate.global.config;

import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;


// Kafka 토픽 생성에 필요한 설정을 추가
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;


// Kafka 토픽 생성에 필요한 설정을 추가
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers[0]}")
    private String node1; // 첫 번째 Kafka 서버 주소

    @Value("${spring.kafka.bootstrap-servers[1]}")
    private String node2; // 두 번째 Kafka 서버 주소

    @Value("${spring.kafka.bootstrap-servers[2]}")
    private String node3; // 세 번째 Kafka 서버 주소

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                "bootstrap.servers", String.join(",", node1, node2, node3) // 각 노드를 쉼표로 연결
        ));
    }

    @Bean
    public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }
}
