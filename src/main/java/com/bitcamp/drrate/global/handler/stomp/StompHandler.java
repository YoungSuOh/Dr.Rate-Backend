package com.bitcamp.drrate.global.handler.stomp;


import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final UsersRepository usersRepository;
    private final JWTUtil jwtUtil;

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
        // STOMP 메시지 헤더에서 Authorization 토큰 추출
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException(ErrorStatus.SESSION_HEADER_NOT_FOUND.getMessage());
        }

        token = token.substring(7);
        try {
            if (jwtUtil.isExpired(token)) {
                throw new IllegalArgumentException(ErrorStatus.SESSION_ACCESS_EXPIRED.getMessage());
            }

            String userId = jwtUtil.getUserId(token);

            if (usersRepository.findUsersById(Long.parseLong(userId)).isEmpty()) {
                throw new IllegalArgumentException(ErrorStatus.USER_ID_CANNOT_FOUND.getMessage());
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(ErrorStatus.SESSION_ACCESS_NOT_VALID.getMessage(), e);
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith("/sub/chat/")) {
            throw new IllegalArgumentException(ErrorStatus.INQUIRE_INVALID_PATH.getMessage());
        }
    }
    /* 세션 활성화되면 추후 수정 예정  */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        log.info("세션 종료: {}", sessionId);
    }

}
