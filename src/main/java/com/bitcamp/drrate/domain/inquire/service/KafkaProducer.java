package com.bitcamp.drrate.domain.inquire.service;


import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper를 DI로 주입받아 재사용

    public void sendMessage(ChatRoom chatRoom, String message, String senderId) {
        try {
            // roomId와 message를 포함한 JSON 객체 생성
            Map<String, String> messagePayload = Map.of(
                    "roomId", chatRoom.getId(),
                    "message", message,
                    "senderId" , senderId
            );

            // JSON 직렬화
            String jsonMessage = objectMapper.writeValueAsString(messagePayload);

            // Kafka에 메시지 발행
            kafkaTemplate.send(chatRoom.getTopicName(), jsonMessage);
            System.out.println("Produced message to Kafka: " + jsonMessage);
        } catch (Exception e) {
            System.err.println("Error sending message to Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
}