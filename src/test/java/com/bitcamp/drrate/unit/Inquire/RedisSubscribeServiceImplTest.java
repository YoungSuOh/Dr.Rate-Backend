package com.bitcamp.drrate.unit.Inquire;

import com.bitcamp.drrate.domain.inquire.dto.response.ChatMessageDTO;
import com.bitcamp.drrate.domain.inquire.service.subscribe.RedisSubscribeServiceImpl;
import com.bitcamp.drrate.domain.inquire.service.websocket.WebSocketMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisSubscribeServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketMessageService webSocketMessageService;

    @InjectMocks
    private RedisSubscribeServiceImpl redisSubscribeService;

    @Test
    void testSendMessage_validMessage() throws JsonProcessingException {
        // Given
        String publishMessage = "{\"roomId\":\"1\",\"message\":\"Hello, Redis!\"}";
        ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                .roomId("1")
                .message("Hello, Redis!")
                .build();

        when(objectMapper.readValue(publishMessage, ChatMessageDTO.class)).thenReturn(chatMessageDTO);

        // When
        redisSubscribeService.sendMessage(publishMessage);

        // Then
        verify(webSocketMessageService).sendToWebSocket("/sub/chat/1", chatMessageDTO);
    }

    @Test
    void testSendMessage_invalidJson() throws JsonProcessingException {
        // Given
        String invalidMessage = "Invalid JSON";

        when(objectMapper.readValue(invalidMessage, ChatMessageDTO.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        // When & Then
        assertThrows(RuntimeException.class, () -> redisSubscribeService.sendMessage(invalidMessage));
    }
}
