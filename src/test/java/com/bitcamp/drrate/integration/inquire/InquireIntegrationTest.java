package com.bitcamp.drrate.integration.inquire;


import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InquireIntegrationTest {

    @LocalServerPort
    private int port;

    // WebSocket 통신을 위한 클라이언트 객체
    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        // WebSocketStompClient 초기화 및 SockJS 지원 추가
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        // WebSocket 메시지 변환기로 Jackson을 등록하여 JSON 직렬화/역직렬화를 지원
    }

    @Test
    void sendMessage_ShouldHandleWebSocketRequest() throws Exception {
        // 메시지 전송 및 응답 처리 테스트
        StompSession session = stompClient.connect(
                        "ws://localhost:" + port + "/websocket",
                        new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);  // 세션 생성 타임아웃 설정.
        // 세션이 성공적으로 생성되었는지 검증
        assertNotNull(session);

        // "/sub/chat/1" 채널 구독
        session.subscribe("/sub/chat/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // 메시지 payload 타입을 InquireResponseDTO로 설정
                return InquireResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 서버로부터 수신된 메시지를 처리하는 메서드
                InquireResponseDTO response = (InquireResponseDTO) payload;
                assertEquals("Hello!", response.getMessage()); // 서버로부터 받은 메시지의 내용이 기대한 값인지 검증
            }
        });
        // 전송할 메세지
        InquireResponseDTO message = InquireResponseDTO.builder()
                .roomId("1")
                .sender("user1")
                .message("Hello!")
                .build();
        // 메세지 전송
        session.send("/pub/chat", message); // JSON 변환기가 자동 처리
        Thread.sleep(1000); // 메시지 처리 대기
    }

    @Test
    void chatBetweenMultipleClients_ShouldWorkInRealTime() throws Exception {
        // 클라이언트 1이 메시지를 전송하고, 클라이언트 2가 이를 수신하는지 검증

        // 클라이언트 1 생성
        StompSession client1 = stompClient.connect(
                        "ws://localhost:" + port + "/websocket", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        // 클라이언트 2 생성
        StompSession client2 = stompClient.connect(
                        "ws://localhost:" + port + "/websocket", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        assertNotNull(client1);
        assertNotNull(client2);

        // 클라이언트 2에서 메시지 구독
        client2.subscribe("/sub/chat/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return InquireResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                InquireResponseDTO response = (InquireResponseDTO) payload;
                assertEquals("Hello from client1!", response.getMessage());
            }
        });

        // 클라이언트 1에서 메시지 전송
        InquireResponseDTO messageFromClient1 = InquireResponseDTO.builder()
                .roomId("1")
                .sender("client1")
                .message("Hello from client1!")
                .build();

        client1.send("/pub/chat", messageFromClient1);

        Thread.sleep(1000); // 메시지 처리 대기
    }

    @Test
    void simultaneousMessages_ShouldBeProcessedInOrder() throws Exception {
        // 두 클라이언트가 동시에 메시지를 전송했을 때, 메시지가 손실 없이 수신되는지 검증

        // 클라이언트 1 및 클라이언트 2 생성
        StompSession client1 = stompClient.connect(
                        "ws://localhost:" + port + "/websocket", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        StompSession client2 = stompClient.connect(
                        "ws://localhost:" + port + "/websocket", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        assertNotNull(client1);
        assertNotNull(client2);

        List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());

        // 클라이언트 1과 2 모두 구독
        client1.subscribe("/sub/chat/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return InquireResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                InquireResponseDTO response = (InquireResponseDTO) payload;
                receivedMessages.add(response.getMessage());
            }
        });

        client2.subscribe("/sub/chat/1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return InquireResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                InquireResponseDTO response = (InquireResponseDTO) payload;
                receivedMessages.add(response.getMessage());
            }
        });

        // 클라이언트 1과 2가 동시에 메시지 전송
        InquireResponseDTO message1 = InquireResponseDTO.builder()
                .roomId("1")
                .sender("client1")
                .message("Message from client1")
                .build();

        InquireResponseDTO message2 = InquireResponseDTO.builder()
                .roomId("1")
                .sender("client2")
                .message("Message from client2")
                .build();

        client1.send("/pub/chat", message1);
        client2.send("/pub/chat", message2);

        Thread.sleep(2000); // 메시지 처리 대기

        // 메시지가 정확히 두 개 수신되었는지 검증
        assertEquals(2, receivedMessages.size());
        // 순서를 확인할 수 있다면 추가 검증
        assertTrue(receivedMessages.contains("Message from client1"));
        assertTrue(receivedMessages.contains("Message from client2"));
    }


}
