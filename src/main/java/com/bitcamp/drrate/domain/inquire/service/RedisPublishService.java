package com.bitcamp.drrate.domain.inquire.service;

import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import org.springframework.data.redis.listener.ChannelTopic;

public interface RedisPublishService {
    public void publish(ChannelTopic topic, InquireResponseDTO message);
}
