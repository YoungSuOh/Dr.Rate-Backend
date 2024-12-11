package com.bitcamp.drrate.domain.inquire.service;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final KafkaTopicService kafkaTopicService;

    public ChatRoom getOrCreateChatRoom(String roomId) {
        // 기존 채팅방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findById(roomId);
        if (existingRoom.isPresent()) {
            System.out.println("Existing ChatRoom found: " + existingRoom.get().getTopicName());
            return existingRoom.orElse(null);
        }

        // 새로운 채팅방 생성
        String topicName = "chat-room-" + roomId;
        ChatRoom newRoom = new ChatRoom();
        newRoom.setId(roomId);
        newRoom.setTopicName(topicName);
        newRoom.setCreatedAt(LocalDateTime.now());

        // MongoDB 저장
        chatRoomRepository.save(newRoom);

        // Kafka 토픽 생성
        kafkaTopicService.createTopic(topicName);

        System.out.println("New ChatRoom created: " + topicName);
        return newRoom;
    }
}