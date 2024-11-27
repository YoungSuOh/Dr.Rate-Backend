package com.bitcamp.drrate.global.code.resultCode;


import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.code.ErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements ErrorCode {
    // user
    USER_ID_UNAVAILABLE(HttpStatus.OK,"USERID400", "중복된 id입니다."),
    USER_JOIN_ERROR(HttpStatus.OK,"USER401", "회원 가입 실패"),
    USER_LOGIN_ERROR(HttpStatus.OK,"USERID402", "로그인 실패"),
    USER_ID_CANNOT_FOUND(HttpStatus.OK,"USERID403", "유효하지 않은 id입니다."),


    // inquire
    INQUIRE_INVALID_PATH(HttpStatus.OK, "INQUIRE401", "잘못된 경로입니다."),
    INQUIRE_INVALID_ARGUMENT(HttpStatus.OK, "INQUIRE402", "topic 또는 message가 null입니다."),
    INQUIRE_ROOM_OVERFLOW(HttpStatus.OK, "INQUIRE403", "채팅방 인원이 초과되었습니다."),
    INQUIRE_ROOM_NOT_FOUND(HttpStatus.OK, "INQUIRE404", "존재하지 않는 채팅방입니다."),
    INQUIRE_ROUTE_NOT_FOUND(HttpStatus.OK, "INQUIRE405", "유효하지 않은 구독 경로입니다."),


    // Session
    SESSION_HEADER_NOT_FOUND(HttpStatus.OK, "SESSION400", "헤더에 세션 정보가 존재하지 않습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL501","서버 오류")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorDTO getReason() {
        return ErrorDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorDTO getHttpStatusReason() {
        return ErrorDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
