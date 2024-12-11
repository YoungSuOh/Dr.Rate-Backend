package com.bitcamp.drrate.domain.inquire.controller;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.ChatRoomService;
import com.bitcamp.drrate.domain.inquire.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InquireController {
    private final KafkaProducer kafkaProducer;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/room/{roomId}") // 클라이언트에서 발행하는 경로
    @SendTo("/sub/chat/room/{roomId}")     // 클라이언트로 브로드캐스팅하는 경로
    public void handleChatMessage(@DestinationVariable String roomId, ChatMessage chatMessage) {
        System.out.println("Received message: " + chatMessage);

        // 1:1 문의용 ChatRoom 생성 또는 조회
        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(roomId);

        // Kafka에 메시지 발행
        kafkaProducer.sendMessage(chatRoom.getTopicName(), chatMessage.getContent(), roomId);

        // WebSocket으로 클라이언트에 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, chatMessage);
    }
}
