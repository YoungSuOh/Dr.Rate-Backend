package com.bitcamp.drrate.domain.inquire.service;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topicPattern =  "chat-room-.*", groupId = "chat_group")
    public void consume(String jsonMessage) {
        try {
            System.out.println(jsonMessage);
            // JSON 문자열을 Map으로 변환
            Map<String, String> messagePayload = objectMapper.readValue(jsonMessage, new TypeReference<Map<String, String>>() {});
            System.out.println("hello");
            // roomId와 메시지 추출
            String roomId = messagePayload.get("roomId");
            String messageContent = messagePayload.get("message");
            String senderId  = messagePayload.get("senderId");

            // ChatMessage 객체 생성
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(roomId);
            chatMessage.setContent(messageContent);
            chatMessage.setSenderId(senderId);

            // MongoDB에 저장
            chatMessageRepository.save(chatMessage);

            // WebSocket으로 클라이언트에 메시지 브로드캐스팅
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, chatMessage);

            System.out.println("Broadcasted message to roomId " + roomId + ": " + chatMessage);
        } catch (Exception e) {
            System.err.println("Error consuming message from Kafka: " + e.getMessage());
        }
    }
}
