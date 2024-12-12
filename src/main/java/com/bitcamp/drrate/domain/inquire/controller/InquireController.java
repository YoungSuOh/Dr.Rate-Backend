package com.bitcamp.drrate.domain.inquire.controller;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.ChatRoomService;
import com.bitcamp.drrate.domain.inquire.service.KafkaProducer;
import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.s3.service.S3Service;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class InquireController {
    private final KafkaProducer kafkaProducer;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Service s3Service;

    @MessageMapping("/chat/room") // 클라이언트에서 발행하는 경로
    @SendTo("/sub/chat/room")     // 클라이언트로 브로드캐스팅하는 경로
    public void handleChatMessage(ChatMessage chatMessage, StompHeaderAccessor accessor) {
        System.out.println("accessor : "+accessor);
        String senderId = String.valueOf(accessor.getSessionAttributes().get("userId"));

        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(senderId);

        // Kafka에 메시지 발행
        kafkaProducer.sendMessage(chatRoom, chatMessage.getContent(), senderId);

        // WebSocket으로 클라이언트에 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessage);
    }
    @PostMapping("/chat/upload")
    public ApiResponse<HttpStatus>uploadFile(@RequestParam("file") MultipartFile multipartFile, StompHeaderAccessor accessor) {
        try {
            /* 파일 업로드  -> 일다 보류 */
            String fileUrl = s3Service.uploadFile(multipartFile);




            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.FILE_UPLOAD_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.FILE_UPLOAD_FAILED.getCode(), ErrorStatus.FILE_UPLOAD_FAILED.getMessage(), null);
        }

    }
}
