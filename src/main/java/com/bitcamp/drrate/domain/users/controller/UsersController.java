package com.bitcamp.drrate.domain.users.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitcamp.drrate.domain.oauth.google.service.GoogleService;
import com.bitcamp.drrate.domain.oauth.kakao.service.KakaoService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.service.EmailService;
import com.bitcamp.drrate.domain.users.service.UsersService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsersController {

    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final UsersService usersService;
    private final EmailService emailService;

    //소셜 로그인 인가코드 요청
    @GetMapping("/login/{provider}")
    public void userLogin(HttpServletResponse response, @PathVariable("provider") String provider) throws IOException {
        if(provider.equals("google")){
            googleService.loginGoogle(response);
        }
        else if(provider.equals("kakao")){
            kakaoService.loginKakao(response);
        }
    }

    //소셜 로그인 (DB저장, 토큰발급, Header세팅)
    @GetMapping("/login/oauth2/code/{provider}")
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

    // 인증번호 전송
    @PostMapping("/email/verification-request")
    public ApiResponse<Boolean> sendMessage(@RequestParam("email") String email) {
        try {
            // 사용자에게 인증코드 이메일 전송
            emailService.sendCodeToEmail(email);
            System.out.println("이메일 인증번호 발송 성공");
            return ApiResponse.onSuccess(true, SuccessStatus.USER_VERIFYCATION_EMAIL);
        } catch (UsersServiceExceptionHandler e) {
            return ApiResponse.onFailure(ErrorStatus.UNABLE_TO_SEND_EMAIL.getCode(), ErrorStatus.UNABLE_TO_SEND_EMAIL.getMessage(), null);
        } catch (Exception e) {
            // 예상치 못한 기타 예외 처리
            return ApiResponse.onFailure(
                    ErrorStatus.INTERNAL_SERVER_ERROR.getCode(),
                    ErrorStatus.INTERNAL_SERVER_ERROR.getMessage(),
                    null
            );
        }
    }
    
    // 인증번호 확인
    @GetMapping("/email/verifications")
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

    @GetMapping("/api/userList")
    public ApiResponse<Page<Users>>getUsersList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
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
    
    //내 정보 불러오기
    @PostMapping("/api/myInfo")
    public ApiResponse<Users> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try{
            Users users = usersService.getMyInfo(userDetails.getId());
            return ApiResponse.onSuccess(users, SuccessStatus.USER_MYPAGE_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.USER_ID_CANNOT_FOUND.getCode(), ErrorStatus.USER_ID_CANNOT_FOUND.getMessage(), null);
        }
    }

}