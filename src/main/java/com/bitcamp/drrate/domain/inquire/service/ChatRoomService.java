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

    public ChatRoom getOrCreateChatRoom(String senderId) {
        // 기존 채팅방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findById(senderId);
        if (existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            chatRoom.setUpdatedAt(LocalDateTime.now());
            chatRoomRepository.save(chatRoom);
            return chatRoom;
        }
        // 새로운 채팅방 생성
        String topicName = "chat-room-" + senderId;
        ChatRoom newRoom = new ChatRoom();
        newRoom.setId(senderId);
        newRoom.setTopicName(topicName);


        // MongoDB 저장
        chatRoomRepository.save(newRoom);

        // Kafka 토픽 생성
        kafkaTopicService.createTopic(topicName);

        return newRoom;
    }
}