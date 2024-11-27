package com.bitcamp.drrate.domain.inquire.service.websocket;

public interface WebSocketMessageService {
    public void sendToWebSocket(String destination, Object payload);
}
