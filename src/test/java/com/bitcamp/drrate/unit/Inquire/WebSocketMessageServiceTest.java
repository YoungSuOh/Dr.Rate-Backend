package com.bitcamp.drrate.unit.Inquire;

import com.bitcamp.drrate.domain.inquire.service.websocket.WebSocketMessageService;
import com.bitcamp.drrate.domain.inquire.service.websocket.WebSocketMessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketMessageServiceTest {

    @Mock
    private SimpMessageSendingOperations messageSendingOperations;

    @InjectMocks
    private WebSocketMessageServiceImpl webSocketMessageServiceImpl;

    @Test
    void testSendToWebSocket_success() {
        // Given
        String destination = "/sub/chat/1";
        String payload = "Hello, WebSocket!";

        // When
        webSocketMessageServiceImpl.sendToWebSocket(destination, payload);

        // Then
        verify(messageSendingOperations).convertAndSend(destination, payload);
    }

    @Test
    void testSendToWebSocket_exception() {
        // Given
        String destination = "/sub/chat/1";
        String payload = "Hello, WebSocket!";
        doThrow(new RuntimeException("Test exception"))
                .when(messageSendingOperations).convertAndSend(destination, payload);

        // When
        webSocketMessageServiceImpl.sendToWebSocket(destination, payload);

        // Then
        verify(messageSendingOperations).convertAndSend(destination, payload);
    }
}
