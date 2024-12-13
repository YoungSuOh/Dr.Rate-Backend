package com.bitcamp.drrate.global.handler.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import javax.naming.AuthenticationException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(clientMessage);

        String sessionId = accessor.getSessionId();
        StompCommand command = accessor.getCommand();
        String errorMessage;

        // 예외별 처리
        if (ex instanceof AuthenticationException) {
            errorMessage = "인증에 실패했습니다. 자격 증명을 확인해주세요.";
        } else if (ex instanceof AccessDeniedException) {
            errorMessage = "접근 권한이 없습니다.";
        } else if (ex instanceof MessageConversionException) {
            errorMessage = "잘못된 메시지 형식입니다.";
        } else {
            errorMessage = "예기치 못한 오류가 발생했습니다.";
        }

        errorMessage = String.format("[%s] 명령 처리 중 오류 발생 (세션: %s): %s",
                command != null ? command.name() : "알 수 없음",
                sessionId != null ? sessionId : "알 수 없음",
                errorMessage);

        accessor.setMessage(errorMessage);
        accessor.addNativeHeader("error-code", "WS-001");
        accessor.addNativeHeader("error-description", errorMessage);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}