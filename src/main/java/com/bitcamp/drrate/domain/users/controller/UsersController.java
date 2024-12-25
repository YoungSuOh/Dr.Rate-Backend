package com.bitcamp.drrate.domain.users.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitcamp.drrate.domain.oauth.google.service.GoogleService;
import com.bitcamp.drrate.domain.oauth.kakao.service.KakaoService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.domain.users.service.EmailService;
import com.bitcamp.drrate.domain.users.service.UsersService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsersController {

    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final UsersService usersService;
    private final EmailService emailService;
    private final UsersRepository usersRepository;

    //소셜 로그인 인가코드 요청
    @RequestMapping(value="/api/signIn/{provider}", method=RequestMethod.GET)
    public void userLogin(HttpServletResponse response, @PathVariable("provider") String provider) throws IOException {
        if(provider.equals("google")){
            googleService.loginGoogle(response);
        }
        else if(provider.equals("kakao")){
            kakaoService.loginKakao(response);
        }
    }

    //소셜 로그인 (DB저장, 토큰발급, Header세팅)
    @RequestMapping(value="/api/signIn/oauth2/code/{provider}", method=RequestMethod.GET)
    public ResponseEntity<?> login(@RequestParam("code") String code, @PathVariable("provider") String provider) {
        try {
            String access = null;
            
            
            // Provider별 처리
            if (provider.equals("google")) { //구글
                access = googleService.login(code);
            } else if (provider.equals("kakao")) { //카카오
                access = kakaoService.login(code);
            } else { // 요청 실패시
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.onFailure(
                                ErrorStatus.SOCIAL_URL_NOT_FOUND.getCode(),
                                ErrorStatus.SOCIAL_URL_NOT_FOUND.getMessage(),
                                null
                        ));
            }

            // 리다이렉트 경로 설정. access토큰을 쿼리 파라미터에 포함
            String redirectUrl = "http://localhost:5173/oauthHandler#access=" + access;
    
            // 성공 시 처리
            // 헤더 세팅
            HttpHeaders headers = usersService.tokenSetting(access);

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    //.headers(headers)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .body(ApiResponse.onSuccess(null, SuccessStatus.USER_LOGIN_SUCCESS)); 
    
        } catch (IllegalArgumentException e) {
            // 클라이언트의 잘못된 요청 (서버 요청주소 설정 오류)
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.onFailure(
                            ErrorStatus.SOCIAL_PARAMETERS_INVALID.getCode(),
                            ErrorStatus.SOCIAL_PARAMETERS_INVALID.getMessage(),
                            null
                    ));
        } catch (Exception e) {
            // 서버 내부 오류
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(
                            ErrorStatus.INTERNAL_SERVER_ERROR.getCode(),
                            ErrorStatus.INTERNAL_SERVER_ERROR.getMessage(),
                            null
                    ));
        }
    }
    //이메일 인증 번호 전송
    @RequestMapping(value="/api/email/verify", method=RequestMethod.POST)
    public ApiResponse<Void> sendMessage(@RequestParam("email") String email) {
        try {
            // 이메일 중복 체크
            if (usersRepository.existsByEmail(email)) {
                return ApiResponse.onFailure(
                        ErrorStatus.USER_EMAIL_DUPLICATE.getCode(), // 오류 코드
                        ErrorStatus.USER_EMAIL_DUPLICATE.getMessage(), // 오류 메시지
                        null // 데이터는 없음
                );
            }

            // 인증 코드 전송 로직
            emailService.sendCodeToEmail(email);
            System.out.println("이메일 인증번호 발송 성공");

            return ApiResponse.onSuccess(null, SuccessStatus.USER_VERIFYCATION_EMAIL); // 성공 시
        } catch (UsersServiceExceptionHandler e) {
            // 메일 전송 실패 시
            return ApiResponse.onFailure(
                    ErrorStatus.UNABLE_TO_SEND_EMAIL.getCode(),
                    ErrorStatus.UNABLE_TO_SEND_EMAIL.getMessage(),
                    null
            );
        } catch (Exception e) {
            // 예상치 못한 서버 오류
            return ApiResponse.onFailure(
                    ErrorStatus.INTERNAL_SERVER_ERROR.getCode(),
                    "서버 오류가 발생했습니다.",
                    null
            );
        }
    }

    
    // 인증번호 확인
    @RequestMapping(value="/api/email/verifications", method=RequestMethod.GET)
    public ApiResponse<Boolean> verificationEmail(@RequestParam("email") String email,
                                            @RequestParam("code") String authCode) {
        System.out.println("email = " + email + "\n" + "code = " + authCode);
        try{
            boolean response = emailService.verifiedCode(email, authCode);
            if(response) {
                System.out.println("인증 성공");
                return ApiResponse.onSuccess(response, SuccessStatus.USER_VERIFY_EMAIL_SUCCESS);
            } else {
                System.out.println("인증 실패");
                return ApiResponse.onFailure(ErrorStatus.EMAIL_VERIFICATION_FAILED.getCode(), ErrorStatus.EMAIL_VERIFICATION_FAILED.getMessage(), null);
            }
        } catch(Exception e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value="/api/admin/userList", method=RequestMethod.GET)
    public ApiResponse<Page<Users>>getUsersList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "keyword", required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        try{
            if(usersService.getUserRole(customUserDetails)!= Role.ADMIN){
            //if(customUserDetails.getRole() != Role.ADMIN) {
                throw new UsersServiceExceptionHandler(ErrorStatus.AUTHORIZATION_INVALID);
            }
            Page<Users>result = usersService.getUsersList(page, size, searchType, keyword);
            return ApiResponse.onSuccess(result, SuccessStatus.USER_LIST_GET_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.USER_LIST_GET_FAILED.getCode(), ErrorStatus.USER_LIST_GET_FAILED.getMessage(), null);
        }
    }

    // 회원가입 처리
    @RequestMapping(value="/api/signUp", method=RequestMethod.POST)
    public ResponseEntity<ApiResponse> signUp(@RequestBody @Valid UsersRequestDTO.UsersJoinDTO usersJoinDTO) {
        try {
            // 회원가입 서비스 호출
            usersService.signUp(usersJoinDTO);

            // 회원가입 성공 응답
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.onSuccess(null, SuccessStatus.USER_JOIN_SUCCESS));
        } catch (UsersServiceExceptionHandler e) {

            // 예외 처리: 이메일 중복 (USER_EMAIL_DUPLICATE)
            if (e.getCode() == ErrorStatus.USER_EMAIL_DUPLICATE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ErrorStatus.USER_EMAIL_DUPLICATE.getCode(),
                                ErrorStatus.USER_EMAIL_DUPLICATE.getMessage(), null));
            }

            // 그 외의 예외: 서버 내부 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR.getCode(),
                            ErrorStatus.INTERNAL_SERVER_ERROR.getMessage(), null));
        } catch (Exception e) {
            // 예상치 못한 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR.getCode(),
                            ErrorStatus.INTERNAL_SERVER_ERROR.getMessage(), null));
        }
    }

    @RequestMapping(value="/api/signUp/existId", method=RequestMethod.GET)
    public ResponseEntity<ApiResponse> checkUserId(@RequestParam("userId") String userId) {
        try {
            System.out.println("Checking user_id: " + userId);

            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ErrorStatus.USER_ID_UNAVAILABLE.getCode(),
                                "아이디를 입력해주세요.", null));
            }

            if (usersRepository.existsByUserId(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ErrorStatus.USER_ID_UNAVAILABLE.getCode(),
                                "이미 가입된 아이디입니다.", null));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.onSuccess(null, SuccessStatus.USER_ID_AVAILABLE));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR.getCode(),
                            "서버 오류가 발생했습니다.", null));
        }
    }

    //내 정보 불러오기
    @RequestMapping(value="/api/myInfo", method=RequestMethod.POST)
    public ApiResponse<Users> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try{
            Users users = usersService.getMyInfo(userDetails.getId());
            return ApiResponse.onSuccess(users, SuccessStatus.USER_MYPAGE_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.USER_ID_CANNOT_FOUND.getCode(), ErrorStatus.USER_ID_CANNOT_FOUND.getMessage(), null);
        }
    }

    // //내 정보 수정 페이지
    // @RequestMapping(value="/api/myInfoEdit", method=RequestMethod.POST)
    // public ApiResponse<Users> getMyInfoEdit(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid UsersRequestDTO usersJoinDTO) {
    //     try{
    //         Users users = usersService.getMyInfo(userDetails.getId());
    //     }
    // }

    //토큰 재발급
    @RequestMapping(value="/api/reissue", method=RequestMethod.POST)
    public ApiResponse<?> tokenRefresh(@RequestBody Map<String, String> requestBody) {
        // 요청 본문에서 access_token 추출
        String accessToken = requestBody.get("access_token");
        try {
            if (accessToken == null || accessToken.isEmpty()) {
                // access_token이 없으면 에러 반환
                return ApiResponse.onFailure(ErrorStatus.SESSION_ACCESS_PARSE_ERROR.getCode(), ErrorStatus.SESSION_ACCESS_PARSE_ERROR.getMessage(), null);
            }
            // accesstoken 검증 로직 및 새로운 accesstoken 발급, 토큰이 없으면 에러 반환
            String token = usersService.invalidAccessToken(accessToken);
            
            if (token.equals("") || token.isEmpty() ) {
                return ApiResponse.onFailure(ErrorStatus.SESSION_ACCESS_PARSE_ERROR.getCode(), ErrorStatus.SESSION_ACCESS_PARSE_ERROR.getMessage(), null);
            }
            return ApiResponse.onSuccess(token, SuccessStatus.USER_TOKEN_REISSUE_SUCCESS); // 토큰이 담겨왔으면 토큰 반환
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.SESSION_ACCESS_PARSE_ERROR.getCode(), ErrorStatus.SESSION_ACCESS_PARSE_ERROR.getMessage(), null);
        }
    }
}