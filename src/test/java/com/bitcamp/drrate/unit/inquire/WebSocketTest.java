package com.bitcamp.drrate.unit.inquire;

import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport((WebSocketClient) new StandardWebSocketClient()))));
    }

    @Test
    void testWebSocketMessageFlow() throws Exception {
        StompSession session = stompClient.connect(
                        "ws://localhost:" + port + "/websocket", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        assertNotNull(session);

        session.subscribe("/sub/chat/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return InquireResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                InquireResponseDTO response = (InquireResponseDTO) payload;
                assertEquals("Hello!", response.getMessage());
            }
        });

        session.send("/pub/chat", new InquireResponseDTO("1", "user1", "Hello!", LocalDateTime.now()));
        Thread.sleep(1000); // 메시지 처리 대기
    }
}
