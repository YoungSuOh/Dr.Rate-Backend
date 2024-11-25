package com.bitcamp.drrate.global.handler.stomp;

import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import com.bitcamp.drrate.domain.inquire.repository.InquireRoomRepository;
import com.bitcamp.drrate.domain.inquire.service.RedisSubscribeService;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    // private final JwtTokenProvider jwtTokenProvider; -> JWT 토큰 발급 이후에
    private final InquireRoomRepository inquireRoomRepository;
    private final UsersRepository usersRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final int LIMIT_MEMBER = 2;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        }else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisconnect(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String userIdHeader = accessor.getFirstNativeHeader("userId");
        if (userIdHeader == null) {
            throw new IllegalArgumentException(ErrorStatus.SESSION_HEADER_NOT_FOUND.getMessage());
        }

        Long userId = Long.parseLong(userIdHeader);
        if (usersRepository.findUsersById(userId).isEmpty()) {
            throw new IllegalArgumentException(ErrorStatus.USER_ID_CANNOT_FOUND.getMessage());
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith("/sub/chat/")) {
            throw new IllegalArgumentException(ErrorStatus.INQUIRE_ROUTE_NOT_FOUND.getMessage());
        }

        String roomId = extractRoomId(destination);
        inquireRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new IllegalArgumentException(ErrorStatus.INQUIRE_ROOM_NOT_FOUND.getMessage()));

        // 채팅방 인원 제한 확인
        if (getConnectedUsersCount(roomId) >= LIMIT_MEMBER) {
            throw new IllegalArgumentException(ErrorStatus.INQUIRE_ROOM_OVERFLOW.getMessage());
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();

        // Redis에서 세션 정보를 삭제
        String redisKey = "chatroom:session:" + sessionId;
        redisTemplate.delete(redisKey);

        // 추가 작업: 로그 기록 등
        log.info("Disconnected session: {}", sessionId);
    }

    private String extractRoomId(String destination) {
        // "/sub/chat/1" -> "1" 추출
        return destination.split("/")[3];
    }

    private int getConnectedUsersCount(String roomId) {
        String redisKey = "chatroom:" + roomId + ":users";
        Long size = redisTemplate.opsForSet().size(redisKey);
        return size != null ? size.intValue() : 0;
    }
}
