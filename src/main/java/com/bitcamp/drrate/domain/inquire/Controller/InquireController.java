package com.bitcamp.drrate.domain.inquire.Controller;


import com.bitcamp.drrate.domain.inquire.dto.response.InquireResponseDTO;
import com.bitcamp.drrate.domain.inquire.service.RedisPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InquireController {
    private final RedisPublishService redisPublishService;
    private final ChannelTopic channelTopic;

    @MessageMapping("/chat")
    public void sendMessage(InquireResponseDTO message) {
        // 메시지를 Redis Pub/Sub 채널에 발행
        redisPublishService.publish(new ChannelTopic("/sub/chat/" + message.getRoomId()), message);
    }

}
