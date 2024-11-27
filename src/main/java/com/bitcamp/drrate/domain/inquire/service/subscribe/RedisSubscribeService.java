package com.bitcamp.drrate.domain.inquire.service.subscribe;

public interface RedisSubscribeService {
    void sendMessage(String publishMessage);
}
