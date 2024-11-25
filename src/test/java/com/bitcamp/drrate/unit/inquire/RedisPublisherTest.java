package com.bitcamp.drrate.unit.inquire;

import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import com.bitcamp.drrate.domain.inquire.service.RedisPublishService;
import com.bitcamp.drrate.domain.inquire.service.RedisPublishServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
class RedisPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private RedisPublishService redisPublishService;

    @BeforeEach
    void setUp() {
        // RedisPublishServiceImpl을 redisTemplate Mock과 함께 초기화
        redisPublishService = new RedisPublishServiceImpl(redisTemplate);
    }

    @Test
    void publish_ValidMessage_ShouldSendToRedis() { // 메시지를 올바른 Redis 채널로 발행하는지 검증합니다.
        // Given
        ChannelTopic topic = new ChannelTopic("chatroom");

        InquireResponseDTO message = InquireResponseDTO.builder()
                .roomId("1")
                .sender("user1")
                .message("Hello!")
                .build();

        // When
        redisPublishService.publish(topic, message);

        // Then
        Mockito.verify(redisTemplate, Mockito.times(1)) // 1회 호출되었는지 검증
                .convertAndSend(topic.getTopic(), message); // 발행한 채널(topic.getTopic())과 메시지(message)가 올바른지 확인
    }
}
