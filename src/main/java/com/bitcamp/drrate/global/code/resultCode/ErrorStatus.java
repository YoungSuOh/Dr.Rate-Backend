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
    USER_ID_UNAVAILABLE(HttpStatus.CONFLICT,"USER400", "중복된 사용자 ID입니다."),
    USER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "USER401", "중복된 이메일입니다."),
    USER_JOIN_ERROR(HttpStatus.BAD_REQUEST,"USER402", "회원 가입 중 오류가 발생했습니다."),
    USER_LOGIN_ERROR(HttpStatus.UNAUTHORIZED,"USER403", "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다"),
    USER_ID_CANNOT_FOUND(HttpStatus.NOT_FOUND,"USER404", "사용자를 찾을 수 없습니다. id가 유효하지 않습니다."),
    USER_DELETION_FAILED(HttpStatus.BAD_REQUEST, "USER405", "사용자 계정 삭제에 실패했습니다."),



    // inquire
    INQUIRE_INVALID_PATH(HttpStatus.OK, "INQUIRE401", "잘못된 경로입니다."),
    INQUIRE_INVALID_ARGUMENT(HttpStatus.OK, "INQUIRE402", "topic 또는 message가 null입니다."),
    INQUIRE_ROOM_OVERFLOW(HttpStatus.OK, "INQUIRE403", "채팅방 인원이 초과되었습니다."),
    INQUIRE_ROOM_NOT_FOUND(HttpStatus.OK, "INQUIRE404", "존재하지 않는 채팅방입니다."),
    INQUIRE_ROUTE_NOT_FOUND(HttpStatus.OK, "INQUIRE405", "유효하지 않은 구독 경로입니다."),


    // Session
    SESSION_HEADER_NOT_FOUND(HttpStatus.BAD_REQUEST, "SESSION400", "헤더에 세션 정보가 존재하지 않습니다."),
    SESSION_ACCESS_NOT_VALID(HttpStatus.UNAUTHORIZED, "SESSION401", "액세스 토큰 값이 유효하지 않습니다."),
    SESSION_REFRESH_NOT_VALID(HttpStatus.UNAUTHORIZED, "SESSION402", "리프레쉬 토큰 값이 유효하지 않습니다."),
    SESSION_ACCESS_EXPIRED(HttpStatus.UNAUTHORIZED, "SESSION403", "액세스 토큰이 만료되었습니다."),



    // Object Storage
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3400", "파일을 찾을 수 없습니다"),
    FILE_METADATA_ERROR(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "S3401", "파일 메타데이터를 처리하는 중 오류가 발생했습니다"),
    S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3402", "S3 업로드 중 오류가 발생했습니다."),
    FILE_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "S3403", "파일 처리 중 오류가 발생했습니다."),
    FILE_CONVERSION_ERROR(HttpStatus.BAD_REQUEST, "S3404", "MultipartFile을 파일로 변환하는 중 오류가 발생했습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "S3405", "파일 업로드를 실패했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3406", "파일 삭제를 실패했습니다."),
    FILE_DELETE_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3407", "파일 삭제 도중 알 수 없는 에러가 발생했습니다."),
    FILE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "S3408", "파일 삭제를 실패했습니다."),
    FILE_UNVAILD_URL(HttpStatus.BAD_REQUEST, "S3409", "유효하지 않은 파일 경로입니다."),


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
