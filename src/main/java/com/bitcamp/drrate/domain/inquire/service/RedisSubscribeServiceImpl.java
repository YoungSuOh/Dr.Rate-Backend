package com.bitcamp.drrate.domain.inquire.service;

import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscribeServiceImpl implements RedisSubscribeService, MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Override
    public void sendMessage(String publishMessage) {
        try {
            // JSON 메시지 역직렬화
            InquireResponseDTO inquireResponseDTO = objectMapper.readValue(publishMessage, InquireResponseDTO.class);

            // WebSocket 경로로 메시지 전송
            messageSendingOperations.convertAndSend(
                    "/sub/chat/" + inquireResponseDTO.getRoomId(),
                    inquireResponseDTO
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메세지 전송에 실패하였습니다.", e);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String publishMessage = new String(message.getBody());
        sendMessage(publishMessage); // Redis Pub/Sub 메시지를 처리
    }
}
