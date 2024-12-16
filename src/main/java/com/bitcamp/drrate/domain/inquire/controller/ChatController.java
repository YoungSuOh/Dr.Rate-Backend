package com.bitcamp.drrate.domain.inquire.controller;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.chatroom.ChatRoomServiceImpl;
import com.bitcamp.drrate.domain.inquire.service.kafka.KafkaProducer;
import com.bitcamp.drrate.domain.s3.service.S3Service;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final KafkaProducer kafkaProducer;
    private final ChatRoomServiceImpl chatRoomServiceImpl;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Service s3Service;

    @MessageMapping("/chat/room") // 클라이언트에서 발행하는 경로
    @SendTo("/sub/chat/room")     // 클라이언트로 브로드캐스팅하는 경로
    public void userChatMessage(ChatMessage chatMessage, StompHeaderAccessor accessor) {
        String senderId = String.valueOf(accessor.getSessionAttributes().get("userId"));

        ChatRoom chatRoom = chatRoomServiceImpl.getOrCreateChatRoom(senderId);

        // Kafka에 메시지 발행
        kafkaProducer.sendMessage(chatRoom, chatMessage.getContent(), senderId);

        // WebSocket으로 클라이언트에 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessage);
    }

    @MessageMapping("/chat/room/{id}") // 클라이언트에서 발행하는 경로
    @SendTo("/sub/chat/room")     // 클라이언트로 브로드캐스팅하는 경로
    public void adminChatMessage(@DestinationVariable String id, ChatMessage chatMessage, StompHeaderAccessor accessor) {
        String senderId = String.valueOf(accessor.getSessionAttributes().get("userId"));

        if(!(String.valueOf(accessor.getSessionAttributes().get("role")).equals("ROLE_ADMIN"))){
            throw new InquireServiceHandler(ErrorStatus.AUTHORIZATION_INVALID);
        }

        ChatRoom chatRoom = chatRoomServiceImpl.getOrCreateChatRoom(id);

        // Kafka에 메시지 발행
        kafkaProducer.sendMessage(chatRoom, chatMessage.getContent(), senderId);

        // WebSocket으로 클라이언트에 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessage);
    }

    @PostMapping("/chat/upload")
    public ApiResponse<HttpStatus> userUploadFile(@RequestParam("file") MultipartFile multipartFile, StompHeaderAccessor accessor) {
        try {
            String senderId = String.valueOf(accessor.getSessionAttributes().get("userId"));

            // 파일 업로드
            String fileUrl = s3Service.uploadFile(multipartFile);

            ChatRoom chatRoom = chatRoomServiceImpl.getOrCreateChatRoom(senderId);

            // Kafka에 파일 업로드 메시지 발행
            kafkaProducer.sendMessage(chatRoom, fileUrl, senderId);

            // WebSocket으로 파일 업로드 정보 전달
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(chatRoom.getId());
            chatMessage.setContent(fileUrl);
            chatMessage.setSenderId(senderId);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessage);

            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.FILE_UPLOAD_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.FILE_UPLOAD_FAILED.getCode(), ErrorStatus.FILE_UPLOAD_FAILED.getMessage(), null);
        }
    }
    @PostMapping("/chat/upload/{id}")
    public ApiResponse<HttpStatus> adminUploadFile(@DestinationVariable String id, @RequestParam("file") MultipartFile multipartFile, StompHeaderAccessor accessor) {
        try {
            String senderId = String.valueOf(accessor.getSessionAttributes().get("userId"));

            if (!(String.valueOf(accessor.getSessionAttributes().get("role")).equals("ROLE_ADMIN"))) {
                throw new InquireServiceHandler(ErrorStatus.AUTHORIZATION_INVALID);
            }

            // 파일 업로드
            String fileUrl = s3Service.uploadFile(multipartFile);

            ChatRoom chatRoom = chatRoomServiceImpl.getOrCreateChatRoom(id);

            // Kafka에 파일 업로드 메시지 발행
            kafkaProducer.sendMessage(chatRoom, fileUrl, senderId);

            // WebSocket으로 파일 업로드 정보 전달
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(chatRoom.getId());
            chatMessage.setContent(fileUrl);
            chatMessage.setSenderId(senderId);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessage);

            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.FILE_UPLOAD_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.FILE_UPLOAD_FAILED.getCode(), ErrorStatus.FILE_UPLOAD_FAILED.getMessage(), null);
        }
    }
}
