package com.bitcamp.drrate.domain.inquire.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTopicService {
    private final KafkaAdmin kafkaAdmin;


    public void createTopic(String topicName) {
        NewTopic topic = new NewTopic(topicName, 1, (short) 1);
        kafkaAdmin.createOrModifyTopics(topic);
        System.out.println("Kafka Topic Created: " + topicName);
    }
}