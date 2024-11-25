package com.bitcamp.drrate.domain.inquire.service;

public interface RedisSubscribeService {
    void sendMessage(String publishMessage);
}
