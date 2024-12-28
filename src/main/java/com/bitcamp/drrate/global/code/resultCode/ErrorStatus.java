package com.bitcamp.drrate.global.code.resultCode;


import org.springframework.http.HttpStatus;

import com.bitcamp.drrate.global.code.ErrorCode;
import com.bitcamp.drrate.global.code.ErrorDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements ErrorCode {
    // user
    USER_ID_UNAVAILABLE(HttpStatus.BAD_REQUEST, "USER400", "이미 가입된 아이디입니다."),
    USER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "USER401", "중복된 이메일입니다."),
    USER_JOIN_ERROR(HttpStatus.BAD_REQUEST, "USER402", "회원 가입 중 오류가 발생했습니다."),
    USER_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "USER403", "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_ID_CANNOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "사용자를 찾을 수 없습니다. id가 유효하지 않습니다."),
    USER_DELETION_FAILED(HttpStatus.BAD_REQUEST, "USER405", "사용자 계정 삭제에 실패했습니다."),
    USER_LIST_GET_FAILED(HttpStatus.NOT_FOUND, "USER406", "유저 목록 조회 실패했습니다."),
    USER_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "USER407", "사용자의 인증에 실패하였습니다."),
    USER_LIST_GET_SUCCESS(HttpStatus.NOT_FOUND, "USER408", "유저 목록 조회 실패했습니다."),
    USER_LIST_BAD_REQUEST(HttpStatus.BAD_REQUEST, "USER409", "잘못된 형식의 유저 목록 조회입니다."),



    // Email
    UNABLE_TO_SEND_EMAIL(HttpStatus.BAD_REQUEST, "USER406", "이메일 전송 중 오류가 발생하였습니다."),
    MAIL_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER407", "인증 메일 생성 중 오류가 발생하였습니다."),
    EMAIL_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER408", "이메일 인증에 실패하였습니다."),


    // Session
    SESSION_HEADER_NOT_FOUND(HttpStatus.BAD_REQUEST, "SESSION400", "헤더에 세션 정보가 존재하지 않습니다."),
    SESSION_ACCESS_INVALID(HttpStatus.UNAUTHORIZED, "SESSION401", "액세스 토큰 값이 유효하지 않습니다."),
    SESSION_REFRESH_INVALID(HttpStatus.UNAUTHORIZED, "SESSION402", "리프레쉬 토큰 값이 유효하지 않습니다."),
    SESSION_ACCESS_EXPIRED(HttpStatus.UNAUTHORIZED, "SESSION403", "액세스 토큰이 만료되었습니다."),
    SESSION_ACCESS_PARSE_ERROR(HttpStatus.NOT_FOUND, "SESSION404", "액세스 토큰이 없거나 분석할 수 없습니다."),
    SESSION_FORMAT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SESSION405", "잘못된 형식의 값이 들어왔습니다."),

    // Inquire
    INQUIRE_LIST_GET_FAILED(HttpStatus.NOT_FOUND, "INQUIRE400", "문의 목록 불러오기 실패했습니다."),
    INQUIRE_MESSAGE_GET_FAILED(HttpStatus.NOT_FOUND,"INQUIRE401", "문의 메세지 조회 실패했습니다"),
    INQUIRE_LIST_BAD_REQUEST(HttpStatus.BAD_REQUEST,"INQUIRE402", "잘못된 형식의 문의 목록 요청입니다."),
    INQUIRE_MESSAGE_BAD_REQUEST(HttpStatus.BAD_REQUEST,"INQUIRE403", "잘못된 형식의 문의 메세지 요청입니다."),
    INQUIRE_INVALID_PATH(HttpStatus.BAD_REQUEST,"INQUIRE404", "잘못된 형식의 문의 메세지 요청입니다."),
    INQUIRE_ROOMID_INVALID(HttpStatus.BAD_REQUEST,"INQUIRE405", "잘못된 문의방 요청입니다."),
    INQUIRE_DELETE_FAILED(HttpStatus.NOT_FOUND,"INQUIRE406", "문의 종료를 실패했습니다"),
    INQUIRE_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,"INQUIRE407", "유효하지 않은 id 요청입니다"),

    // VISIT
    VISIT_RECORD_FAILED(HttpStatus.BAD_REQUEST,"VISIT400", "일일 방문자 데이터 저장을 실패했습니다."),
    VISIT_RECORD_LOAD_FAILED(HttpStatus.NOT_FOUND,"VISIT401", "방문자 데이터 불러오기를 실패했습니다."),

    // Kafka
    KAFKA_BROKER_BADREQUEST(HttpStatus.SERVICE_UNAVAILABLE, "KAFKA400", "Kafka에 BROKER에 전송을 실패했습니다."),
    KAFKA_PUBLISH_MESSAGE_BADREQUEST(HttpStatus.BAD_REQUEST, "KAFKA401", "Kafka에 잘못된 형식의 메세지를 Publish를 시도했습니다."),
    KAFKA_SUBSCRIBE_MESSAGE_BADREQUEST(HttpStatus.BAD_REQUEST, "KAFKA402", "Kafka에 잘못된 형식의 메세지를 Subscribe를 시도했습니다."),
    KAFKA_TOPIC_CREATE_BADREQUEST(HttpStatus.BAD_REQUEST, "KAFKA403", "Kafka에 잘못된 Topic 형식의 요청을 했습니다."),
    KAFKA_TOPIC_EXIST_ERROR(HttpStatus.CONFLICT, "KAFKA403", "Kafka에 중복된 Topic이 존재합니다.."),
    KAFKA_TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "KAFKA404", "토픽 조회 실패했습니다."),

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
    S3_DELETE_FAILED(HttpStatus.BAD_REQUEST, "S3409", "S3 삭제 중 오류가 발생했습니다."),

    // Social
    SOCIAL_URL_NOT_FOUND(HttpStatus.BAD_REQUEST, "SESSION405", "잘못된 요청 주소입니다."),
    SOCIAL_PARAMETERS_INVALID(HttpStatus.NOT_FOUND, "SESSION406", "잘못된 매개변수를 설정하였습니다."),
    SOCIAL_AUTHORIZATION_INVALID(HttpStatus.UNAUTHORIZED, "SESSION407", "소셜 로그인 사용자는 정보수정이 불가능 합니다."),


    // Mongo db
    MONGODB_SAVE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "MONGO400", "Mongodb에 저장을 실패했습니다"),
    MONGODB_LOAD_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "MONGO401", "Mongodb에 불러오기를 실패했습니다"),
    MONGODB_DELETE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "MONGO402", "Mongodb에 삭제를 실패했습니다"),

    // mySQL
    MYSQL_LOAD_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "MYSQL401", "MySql에 불러오기를 실패했습니다"),

    // Redis
    REDIS_SAVE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "REDIS400", "Redis에 저장을 실패했습니다"),
    REDIS_LOAD_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "REDIS401", "Redis에 불러오기를 실패했습니다"),


    // PRODUCT
    PRD_ID_ERROR(HttpStatus.NOT_FOUND, "PRD400", "존재하지 않는 상품입니다."),
    PRD_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRD401", "알 수 없는 오류가 발생했습니다."),
    PRD_ID_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "PRD402", "상품 ID 형식이 잘못되었습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD404", "상품을 찾을 수 없습니다"),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD404", "옵션을 찾을 수 없습니다"),
    CONDITIONS_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRD405", "옵션 파싱 중 오류가 발생했습니다"),
    CONDITIONS_SPECIAL_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRD406", "우대조건 파싱 중 오류가 발생했습니다."),
    CONDITIONS_SPECIAL_PARSERATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRD407", "금리 처리 중 오류가 발생했습니다."),
    CONDITIONS_SPECIAL_INSERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRD408", "우대조건 리스트 저장 중 오류가 발생했습니다."),
    CONDITIONS_SPECIAL_UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "PRD409", "우대조건을 가져올 수 없습니다"),
    PRODUCT_BAD_REQUEST(HttpStatus.BAD_REQUEST,"PRD410","잘못된 상품 조회 요청입니다"),


    // INSERTPRODUCT
    INSERT_ALL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INS400", "모든 상품 등록에 실패했습니다."),
    JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INS401", "JSON으로 가져오기에 실패했습니다."),
    INSERT_PRD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INS400", "상품 등록에 실패했습니다."),
    INSERT_DEPOPTIONS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INS400", "예금 상품 등록에 실패했습니다."),
    INSERT_INSOPTIONS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INS400", "적금 상품 등록에 실패했습니다."),

    // Favorite
    FAVORITE_INVALID_USER_ID(HttpStatus.BAD_REQUEST, "FAV400", "유효하지 않은 사용자 ID입니다."),
    FAVORITE_INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "FAV401", "유효하지 않은 상품 ID입니다."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "FAV402", "이미 즐겨찾기에 등록되어 있습니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAV403", "즐겨찾기 데이터를 찾을 수 없습니다."),
    FAVORITE_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FAV404", "즐겨찾기 조회에 실패했습니다."),
    FAVORITE_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FAV405", "즐겨찾기 등록에 실패했습니다."),
    FAVORITE_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FAV406", "즐겨찾기 검색에 실패했습니다."),
    FAVORITE_NO_RESULTS(HttpStatus.OK, "FAV407", "조회된 즐겨찾기 데이터가 없습니다."),
    FAVORITE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FAV408", "즐겨찾기 삭제에 실패했습니다."),
    FAVORITE_PARTIAL_DELETE_FAILED(HttpStatus.PARTIAL_CONTENT, "FAV409", "일부 즐겨찾기 삭제에 실패했습니다."),
    
    // Calendar
    CALENDAR_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "CAL001", "유효하지 않은 요청 데이터입니다."),
    CALENDAR_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAL002", "이벤트 저장에 실패했습니다."),
    CALENDAR_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAL003", "이벤트 조회에 실패했습니다."),
    CALENDAR_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAL004", "이벤트 수정에 실패했습니다."),
    CALENDAR_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAL005", "이벤트 삭제에 실패했습니다."),
    CALENDAR_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CAL006", "해당 이벤트를 찾을 수 없습니다."),
    CALENDAR_BANK_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAL007", "은행 데이터 조회에 실패했습니다."),
    CALENDAR_PRODUCT_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAL008", "적금명 데이터 조회에 실패했습니다."),

    // 권한 에러
    AUTHORIZATION_INVALID(HttpStatus.UNAUTHORIZED, "AUTHORIZATION400", "접근 권한이 없습니다."),



    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL500","서버 오류"),
    JSON_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "GLOBAL501","JSON 변환 오류")
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
