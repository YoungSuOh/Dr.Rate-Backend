package com.bitcamp.drrate.domain.inquire.service.chatroom;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoomImage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoomStatus;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.bitcamp.drrate.domain.inquire.repository.ChatRoomImageRepository;
import com.bitcamp.drrate.domain.inquire.repository.ChatRoomRepository;
import com.bitcamp.drrate.domain.inquire.service.kafka.KafkaTopicService;
import com.bitcamp.drrate.domain.s3.dto.request.FileRequestDTO;
import com.bitcamp.drrate.domain.s3.service.S3Service;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import com.bitcamp.drrate.domain.users.service.UsersService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomImageRepository chatRoomImageRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final KafkaTopicService kafkaTopicService;
    private final UsersService usersService;
    private final S3Service s3Service;

    public ChatRoom getOrCreateChatRoom(String senderId) {
        try {
            // 기존 채팅방 조회
            Optional<ChatRoom> existingRoom = chatRoomRepository.findById(senderId);
            if (existingRoom.isPresent()) {
                ChatRoom chatRoom = existingRoom.get();
                chatRoom.setUpdatedAt(LocalDateTime.now());
                chatRoom.setStatus(ChatRoomStatus.OPEN);
                chatRoomRepository.save(chatRoom);
                return chatRoom;
            }

            // 새로운 채팅방 생성
            String topicName = "chat-room-" + senderId;
            ChatRoom newRoom = new ChatRoom();
            newRoom.setId(senderId);
            newRoom.setTopicName(topicName);

            UsersResponseDTO.ChatRoomUserInfo chatRoomUserInfo = usersService.getChatRoomUserInfo(Long.parseLong(senderId));
            newRoom.setEmail(chatRoomUserInfo.getEmail());
            newRoom.setUserName(chatRoomUserInfo.getName());
            newRoom.setStatus(ChatRoomStatus.OPEN);
            newRoom.setCreatedAt(LocalDateTime.now());
            newRoom.setUpdatedAt(LocalDateTime.now());

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

    public Page<ChatRoom> getChatRoomsBySearchCriteria(int page, int size, String searchType, String keyword) {
        try {
            if (page < 0 || size <= 0) {
                throw new InquireServiceHandler(ErrorStatus.INQUIRE_LIST_BAD_REQUEST);
            }

            Pageable pageable = PageRequest.of(page, size);

            // 검색 조건에 따라 분기
            if (searchType != null && keyword != null) {
                if ("roomId".equalsIgnoreCase(searchType)) { // 방 번호 검색
                    return chatRoomRepository.findByIdContainingIgnoreCaseOrderByUpdatedAt(keyword, pageable);
                } else if ("email".equalsIgnoreCase(searchType)) { // 이메일 검색
                    return chatRoomRepository.findByEmailContainingIgnoreCaseOrderByUpdatedAt(keyword, pageable);
                } else if ("name".equalsIgnoreCase(searchType)) { // 이름 검색
                    return chatRoomRepository.findByUserNameContainingIgnoreCaseOrderByUpdatedAt(keyword, pageable);
                } else {
                    throw new InquireServiceHandler(ErrorStatus.INQUIRE_LIST_BAD_REQUEST);
                }
            }

            // 검색 조건이 없으면 전체 목록 반환
            return chatRoomRepository.findAllByOrderByUpdatedAtDesc(pageable);
        } catch (IllegalArgumentException e) {
            throw new InquireServiceHandler(ErrorStatus.INQUIRE_LIST_BAD_REQUEST);
        } catch (MongoException e) {
            throw new InquireServiceHandler(ErrorStatus.MONGODB_LOAD_FAILED);
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public void deleteChatRoomById(String id) {
        try {
            if (id == null || id.isEmpty()) {
                throw new InquireServiceHandler(ErrorStatus.INQUIRE_ROOMID_INVALID);
            }

            // ChatRoomImage에서 파일 URL 가져오기
            List<ChatRoomImage> chatRoomImages;
            try {
                chatRoomImages = chatRoomImageRepository.findAllById(Collections.singleton(id));
            } catch (MongoException e) {
                throw new InquireServiceHandler(ErrorStatus.MONGODB_LOAD_FAILED);
            }

            // S3에서 파일 삭제
            for (ChatRoomImage image : chatRoomImages) {
                try {
                    FileRequestDTO.FileDeleteRequest deleteRequest = new FileRequestDTO.FileDeleteRequest();
                    deleteRequest.setFileUrl(image.getImageUrl());
                    s3Service.deleteFile(deleteRequest);
                } catch (Exception e) {
                    throw new InquireServiceHandler(ErrorStatus.S3_DELETE_FAILED);
                }
            }

            // MongoDB에서 ChatRoomImage 삭제
            try {
                chatRoomImageRepository.deleteAllById(Collections.singleton(id));
            } catch (MongoException e) {
                throw new InquireServiceHandler(ErrorStatus.MONGODB_DELETE_FAILED);
            }

            // MongoDB에서 ChatMessage 삭제
            try {
                System.out.println("1");
                chatMessageRepository.deleteAllByRoomId(id);
                System.out.println("2");
            } catch (MongoException e) {
                System.out.println("3");
                throw new InquireServiceHandler(ErrorStatus.MONGODB_DELETE_FAILED);
            }

            try {
                Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(id);
                if (chatRoomOptional.isPresent()) {
                    ChatRoom chatRoom = chatRoomOptional.get();
                    chatRoom.setStatus(ChatRoomStatus.CLOSED); // 상태를 CLOSED로 변경
                    chatRoomRepository.save(chatRoom); // 변경된 상태 저장
                }else {
                    throw new InquireServiceHandler(ErrorStatus.INQUIRE_ROOM_NOT_FOUND);
                }

            } catch (MongoException e) {
                throw new InquireServiceHandler(ErrorStatus.MONGODB_DELETE_FAILED);
            }

        } catch (InquireServiceHandler e) {
            throw e;
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
