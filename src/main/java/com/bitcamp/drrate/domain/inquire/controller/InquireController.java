package com.bitcamp.drrate.domain.inquire.controller;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.ChatRoomService;
import com.bitcamp.drrate.domain.inquire.service.KafkaProducer;
import com.bitcamp.drrate.domain.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class InquireController {
    private final KafkaProducer kafkaProducer;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JWTUtil jwtUtil;

    @MessageMapping("/chat/room") // 클라이언트에서 발행하는 경로
    @SendTo("/sub/chat/room")     // 클라이언트로 브로드캐스팅하는 경로
    public void handleChatMessage(ChatMessage chatMessage, StompHeaderAccessor accessor) {
        System.out.println("accessor : "+accessor);
        String role = String.valueOf(accessor.getSessionAttributes().get("role"));
        String senderId = String.valueOf(accessor.getSessionAttributes().get("userId"));


        ChatRoom chatRoom = null;
        // 1:1 문의용 ChatRoom 생성 또는 조회
        if(Objects.equals(role, "ROLE_USER")){
            chatRoom = chatRoomService.getOrCreateChatRoom(senderId);
        }
        // Kafka에 메시지 발행
        kafkaProducer.sendMessage(chatRoom, chatMessage.getContent(), senderId);

        // WebSocket으로 클라이언트에 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessage);
    }
}
