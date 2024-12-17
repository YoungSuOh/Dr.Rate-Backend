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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final KafkaProducer kafkaProducer;
    private final ChatRoomServiceImpl chatRoomServiceImpl;
    private final S3Service s3Service;

    @MessageMapping("/chat/room/{id}") // 클라이언트에서 발행하는 경로
    public void chatMessage(@DestinationVariable String id, ChatMessage chatMessage, StompHeaderAccessor stompHeaderAccessor) {
        String senderId = String.valueOf(stompHeaderAccessor.getSessionAttributes().get("userId"));
        ChatRoom chatRoom = chatRoomServiceImpl.getOrCreateChatRoom(id);
        // Kafka에 메시지 발행
        kafkaProducer.sendMessage(chatRoom, chatMessage.getContent(), senderId);
    }

    @PostMapping("/chat/upload/{id}")
    @ResponseBody
    public ApiResponse<String> uploadFile(
            @PathVariable("id") String id,
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("senderId") String senderId) {
        try {
            System.out.println("id : "+id);
            // 파일 업로드
            String fileUrl = s3Service.uploadFile(multipartFile);

            ChatRoom chatRoom = chatRoomServiceImpl.getOrCreateChatRoom(id);

            // Kafka에 파일 업로드 메시지 발행
            kafkaProducer.sendUploadFile(chatRoom, fileUrl, senderId);

            return ApiResponse.onSuccess(fileUrl, SuccessStatus.FILE_UPLOAD_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.FILE_UPLOAD_FAILED.getCode(), ErrorStatus.FILE_UPLOAD_FAILED.getMessage(), null);
        }
    }
}
