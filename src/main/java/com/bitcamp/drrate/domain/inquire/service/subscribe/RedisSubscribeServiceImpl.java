package com.bitcamp.drrate.domain.inquire.service.subscribe;

import com.bitcamp.drrate.domain.inquire.dto.response.ChatMessageDTO;
import com.bitcamp.drrate.domain.inquire.service.websocket.WebSocketMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscribeServiceImpl implements RedisSubscribeService, MessageListener {
    private final ObjectMapper objectMapper;
    private final WebSocketMessageService webSocketMessageService; // WebSocket 전송 서비스

    @Override
    public void sendMessage(String publishMessage) {
        try {
            // JSON 메시지 역직렬화
            ChatMessageDTO inquireResponseDTO = objectMapper.readValue(publishMessage, ChatMessageDTO.class);

            // WebSocket 경로로 메시지 전송 위임
            webSocketMessageService.sendToWebSocket(
                    "/sub/chat/" + inquireResponseDTO.getRoomId(),
                    inquireResponseDTO
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 전송에 실패하였습니다.", e);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String publishMessage = new String(message.getBody());
        try {
            sendMessage(publishMessage);
        } catch (RuntimeException e) {
            log.error("Redis 메시지 처리 중 에러 발생: {}", e.getMessage(), e);
        }
    }
}
