package com.bitcamp.drrate.domain.inquire.service.chatroom;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.repository.ChatRoomRepository;
import com.bitcamp.drrate.domain.inquire.service.kafka.KafkaTopicService;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final KafkaTopicService kafkaTopicService;

    public ChatRoom getOrCreateChatRoom(String senderId) {
        try {
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
            try {
                chatRoomRepository.save(newRoom);
            } catch (MongoException e) {
                throw new InquireServiceHandler(ErrorStatus.MONGODB_SAVE_FAILED);
            }

            // Kafka 토픽 생성
            try {
                kafkaTopicService.createTopic(topicName);
            } catch (KafkaException e) {
                throw new InquireServiceHandler(ErrorStatus.KAFKA_BROKER_BADREQUEST);
            }

            return newRoom;
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Page<ChatRoom> getChatRoomsSortedByUpdatedAt(int page, int size) {
        try {
            if (page < 0 || size <= 0) {
                throw new InquireServiceHandler(ErrorStatus.INQUIRE_LIST_BAD_REQUEST);
            }
            Pageable pageable = PageRequest.of(page, size);
            return chatRoomRepository.findAllByOrderByUpdatedAtDesc(pageable);
        } catch (IllegalArgumentException e) {
            throw new InquireServiceHandler(ErrorStatus.INQUIRE_LIST_BAD_REQUEST);
        } catch (MongoException e) {
            throw new InquireServiceHandler(ErrorStatus.MONGODB_LOAD_FAILED);
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
