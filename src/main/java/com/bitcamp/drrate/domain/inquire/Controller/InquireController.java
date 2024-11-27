package com.bitcamp.drrate.domain.inquire.Controller;


import com.bitcamp.drrate.domain.inquire.dto.response.ChatMessageDTO;
import com.bitcamp.drrate.domain.inquire.service.publish.RedisPublishService;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InquireController {
    private final RedisPublishService redisPublishService;
    private final UsersRepository usersRepository;

    @MessageMapping("/chat")
    public void sendMessage(/*@RequestHeader("Authorization") String token,*/ ChatMessageDTO chatMessageDTO) {
        /* JWT 토큰 필요 */
        // String accessToken = token.replace("Bearer ", "");
        // Claims claims = tokenService.parseToken(accessToken);
        // Long userId = Long.parseLong(claims.get("userId", String.class));
        // String role = claims.get("role", String.class);
        /*Users sender = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));*/
        ChannelTopic channelTopic = redisPublishService.getOrCreateChannel(chatMessageDTO.getRoomId());

        // 메시지를 Redis Pub/Sub 채널에 발행
        redisPublishService.publish(channelTopic
                /*, sender*/,
                chatMessageDTO/*,
                sender*/
        );
        log.info("메시지가 Redis Pub/Sub에 발행되었습니다. RoomId={}, Message={}", chatMessageDTO.getRoomId(), chatMessageDTO);
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestBody ChatMessageDTO chatMessageDTO) {
        ChannelTopic channelTopic = redisPublishService.getOrCreateChannel(chatMessageDTO.getRoomId());

        // Redis Pub/Sub에 메시지 발행
        redisPublishService.publish(channelTopic, chatMessageDTO);

        log.info("메시지가 Redis Pub/Sub에 발행되었습니다. RoomId={}, Message={}", chatMessageDTO.getRoomId(), chatMessageDTO);
        return ResponseEntity.ok("메시지 발행 완료");
    }

}
