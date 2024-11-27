package com.bitcamp.drrate.domain.inquire.service.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageServiceImpl implements WebSocketMessageService {
    private final SimpMessageSendingOperations messageSendingOperations;

    public void sendToWebSocket(String destination, Object payload) {
        try {
            messageSendingOperations.convertAndSend(destination, payload);
            log.info("메시지 전송 완료: destination={}, payload={}", destination, payload);
        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }
}