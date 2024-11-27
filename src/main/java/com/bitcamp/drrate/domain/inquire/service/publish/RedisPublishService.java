package com.bitcamp.drrate.domain.inquire.service.publish;

import com.bitcamp.drrate.domain.inquire.dto.response.ChatMessageDTO;
import org.springframework.data.redis.listener.ChannelTopic;

public interface RedisPublishService {
    public void publish(ChannelTopic topic, ChatMessageDTO message);
    public ChannelTopic getOrCreateChannel(String roomId);
}
