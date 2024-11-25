package com.bitcamp.drrate.unit.inquire;

import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import com.bitcamp.drrate.domain.inquire.service.RedisSubscribeServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@ExtendWith(MockitoExtension.class)
class RedisSubscribeServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SimpMessageSendingOperations messageSendingOperations;

    @InjectMocks
    private RedisSubscribeServiceImpl redisSubscribeServiceImpl;

    @Test
    void sendMessage_ValidMessage_ShouldSendToWebSocket() throws Exception {
        // 유효한 메시지를 WebSocket으로 전송하는지 검증

        // Given

        // Redis로부터 수신된 JSON 메시지 -> 직렬화된 메세지
        String publishMessage = "{\"roomId\":\"1\", \"sender\":\"user1\", \"message\":\"Hello!\"}";
        InquireResponseDTO mockMessage = InquireResponseDTO.builder()
                .roomId("1")
                .sender("user1")
                .message("Hello!")
                .build();
        //  ObjectMapper의 동작을 Mocking : DTO 객체로 역직렬화
        Mockito.when(objectMapper.readValue(publishMessage, InquireResponseDTO.class))
                .thenReturn(mockMessage);

        // When

        // Redis에서 받은 메시지를 WebSocket으로 전송
        redisSubscribeServiceImpl.sendMessage(publishMessage);

        // Then

        // WebSocket 메시지 전송 검증
        Mockito.verify(messageSendingOperations, Mockito.times(1)) // 한 번 호출되었는지 확인
                .convertAndSend("/sub/chat/1", mockMessage); // 해당 채팅방에 전송되었는지 확인
    }

    @Test
    void sendMessage_InvalidMessage_ShouldThrowException() throws Exception {
        // 잘못된 JSON 메시지가 처리 중 예외를 발생시키는지 검증

        // Given
        String invalidMessage = "invalid-json";

        Mockito.when(objectMapper.readValue(invalidMessage, InquireResponseDTO.class))
                .thenThrow(JsonProcessingException.class);

        // When & Then
        Assertions.assertThrows(RuntimeException.class, () ->
                redisSubscribeServiceImpl.sendMessage(invalidMessage)
        );
    }
}
