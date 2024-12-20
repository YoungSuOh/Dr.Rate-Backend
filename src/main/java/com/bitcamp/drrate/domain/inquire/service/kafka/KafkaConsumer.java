package com.bitcamp.drrate.domain.inquire.service.kafka;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
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

    @KafkaListener(topicPattern = "chat-room-.*", groupId = "chat_group")
    public void consume(String jsonMessage) {
        try {
            System.out.println(jsonMessage);

            // JSON 문자열을 Map으로 변환
            Map<String, String> messagePayload = objectMapper.readValue(jsonMessage, new TypeReference<Map<String, String>>() {});

            // 필수 필드 검증
            String roomId = messagePayload.get("roomId");
            String messageContent = messagePayload.get("message");
            String senderId = messagePayload.get("senderId");

            if (roomId == null || messageContent == null || senderId == null) {
                throw new InquireServiceHandler(ErrorStatus.KAFKA_SUBSCRIBE_MESSAGE_BADREQUEST);
            }

            // ChatMessage 객체 생성
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(roomId);
            chatMessage.setContent(messageContent);
            chatMessage.setSenderId(senderId);
            chatMessage.setCreatedAt(LocalDateTime.now());

            // MongoDB에 저장
            try {
                chatMessageRepository.save(chatMessage);
            } catch (MongoException e) {
                throw new InquireServiceHandler(ErrorStatus.MONGODB_SAVE_FAILED);
            }

            // WebSocket으로 클라이언트에 메시지 브로드캐스팅
            try {
                messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, chatMessage);
            } catch (MessagingException e) {
                throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (JsonProcessingException e) {
            throw new InquireServiceHandler(ErrorStatus.JSON_PROCESSING_ERROR
            );
        } catch (KafkaException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_BROKER_BADREQUEST);
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
