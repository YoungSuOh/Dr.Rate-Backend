package com.bitcamp.drrate.domain.inquire.service.kafka;


import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoomImage;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.bitcamp.drrate.domain.inquire.repository.ChatRoomImageRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper를 DI로 주입받아 재사용
    private final ChatRoomImageRepository chatRoomImageRepository;
    private final ChatMessageRepository chatMessageRepository;

    public void sendMessage(ChatRoom chatRoom, String message, String senderId) {
        try {
            System.out.println("message: " + message);
            // roomId와 message를 포함한 JSON 객체 생성
            Map<String, String> messagePayload = Map.of(
                    "roomId", chatRoom.getId(),
                    "message", message,
                    "senderId" , senderId
            );
            System.out.println("roomId : " + chatRoom.getId()+" message: " + message+" senderId: " + senderId);

            // JSON 직렬화
            String jsonMessage = objectMapper.writeValueAsString(messagePayload);
            // Kafka에 메시지 발행
            kafkaTemplate.send(chatRoom.getTopicName(), jsonMessage);

        } catch (JsonProcessingException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_PUBLISH_MESSAGE_BADREQUEST);
        } catch (KafkaException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_BROKER_BADREQUEST);
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void sendUploadFile(ChatRoom chatRoom, String imageUrl, String senderId) {
        try {
            System.out.println("message: " + imageUrl);
            // roomId와 message를 포함한 JSON 객체 생성
            Map<String, String> messagePayload = Map.of(
                    "roomId", chatRoom.getId(),
                    "message", imageUrl,
                    "senderId" , senderId
            );

            // JSON 직렬화
            String jsonMessage = objectMapper.writeValueAsString(messagePayload);
            // Kafka에 메시지 발행
            kafkaTemplate.send(chatRoom.getTopicName(), jsonMessage);

            chatRoomImageRepository.save(ChatRoomImage.builder()
                    .id(chatRoom.getId())
                    .imageUrl(imageUrl)
                    .build());
        } catch (JsonProcessingException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_PUBLISH_MESSAGE_BADREQUEST);
        } catch (KafkaException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_BROKER_BADREQUEST);
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}