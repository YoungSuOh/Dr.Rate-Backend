package com.bitcamp.drrate.domain.users.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.drrate.domain.oauth.google.service.GoogleService;
import com.bitcamp.drrate.domain.oauth.kakao.service.KakaoService;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.domain.users.service.EmailService;
import com.bitcamp.drrate.domain.users.service.UsersService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final UsersService usersService;
    private final EmailService emailService;
    /* 임시 */
    private final UsersRepository usersRepository;

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
        if(provider.equals("google")){
            String access = googleService.login(code);
            HttpHeaders headers = usersService.tokenSetting(access);

            return ResponseEntity.ok().headers(headers).build();
        }
        else if(provider.equals("kakao")){
            String access = kakaoService.login(code);
            HttpHeaders headers = usersService.tokenSetting(access);

            return ResponseEntity.ok().headers(headers).build();
        }
        else return ResponseEntity.badRequest().build();
    }
    // 인증번호 전송
    @PostMapping("/email/verification-request")
    public ApiResponse<Boolean> sendMessage(@RequestParam("email") String email) {
        //사용자에게 인증코드 이메일 전송
        emailService.sendCodeToEmail(email);
        System.out.println("이메일 인증번호 발송 성공");
        return ApiResponse.onSuccess(true, SuccessStatus.USER_VERIFYCATION_EMAIL);
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

    /* 임시 !!*/
    @GetMapping("/api/user/{id}") @ResponseBody
    public String usersTest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
        System.out.println("id : "+id);
        Optional<Users> user = usersRepository.findUsersById(Long.valueOf(id));
        if(user.isPresent()){
            return "exist";
        }else{
            return "not exist";
        }
    }


}