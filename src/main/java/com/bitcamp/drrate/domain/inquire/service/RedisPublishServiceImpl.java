package com.bitcamp.drrate.domain.inquire.service;

import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublishServiceImpl implements  RedisPublishService {
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void publish(ChannelTopic topic, InquireResponseDTO message) { // message를 특정 채널에 발행
        if (topic == null || message == null) {
            throw new IllegalArgumentException(ErrorStatus.INQUIRE_INVALID_ARGUMENT.getMessage());
        }
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
