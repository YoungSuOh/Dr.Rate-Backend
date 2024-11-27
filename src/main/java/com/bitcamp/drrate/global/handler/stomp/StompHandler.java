package com.bitcamp.drrate.global.handler.stomp;

import com.bitcamp.drrate.domain.inquire.repository.InquireRoomRepository;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final int LIMIT_MEMBER = 2;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
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
            throw new IllegalArgumentException(ErrorStatus.INQUIRE_INVALID_PATH.getMessage());
        }

        // 채팅방 ID 추출
        String roomIdStr = destination.replace("/sub/chat/", "");
        Long roomId = Long.parseLong(roomIdStr);

        // 채팅방 존재 여부 확인
        inquireRoomRepository.findById(roomId).orElseThrow(() ->
                new IllegalArgumentException(ErrorStatus.INQUIRE_ROOM_NOT_FOUND.getMessage()));
    }
    /* 세션 활성화되면 추후 수정 예정  */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        log.info("세션 종료: {}", sessionId);
    }
}
