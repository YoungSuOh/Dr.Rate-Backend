package com.bitcamp.drrate.global.handler.stomp;


import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.StompServiceExceptionHandler;
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
        log.info("연결 세션 id: {}", accessor.getSessionId());
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 누락되었거나 잘못됨");
            throw new StompServiceExceptionHandler(ErrorStatus.SESSION_HEADER_NOT_FOUND);
        }

        token = token.substring(7);
        try {
            if (jwtUtil.isExpired(token)) {
                log.error("JWT 토큰이 만료됨");
                throw new StompServiceExceptionHandler(ErrorStatus.SESSION_ACCESS_EXPIRED);
            }

            Long userId = jwtUtil.getId(token);

            if (usersRepository.findUsersById(userId).isEmpty()){
                log.error("사용자를 찾을 수 없음");
                throw new StompServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND);
            }
            String role = jwtUtil.getRole(token);
            // 세션에 userId & role 저장
            accessor.getSessionAttributes().put("userId", userId);
            accessor.getSessionAttributes().put("role", role);

        } catch (Exception e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            throw new StompServiceExceptionHandler(ErrorStatus.SESSION_ACCESS_INVALID);
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        log.info("구독 세션 id: {}", accessor.getSessionId());
        if (destination == null || !destination.startsWith("/sub/chat/")) {
            throw new StompServiceExceptionHandler(ErrorStatus.INQUIRE_INVALID_PATH);
        }
    }
    /* 세션 활성화되면 추후 수정 예정  */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        log.info("세션 종료: {}", sessionId);
    }

}
