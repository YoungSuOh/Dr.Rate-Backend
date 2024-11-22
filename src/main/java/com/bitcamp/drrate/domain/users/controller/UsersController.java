package com.bitcamp.drrate.domain.users.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bitcamp.drrate.domain.google.service.GoogleService;
import com.bitcamp.drrate.domain.kakao.service.KakaoService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final GoogleService googleService;
    private final KakaoService kakaoService;

    @GetMapping("/login/{provider}")
    public void userLogin(HttpServletResponse response, @PathVariable("provider") String provider) throws IOException {
        if(provider.equals("google")){
            googleService.loginGoogle(response);
        }
        else if(provider.equals("kakao")){
            kakaoService.loginKakao(response);
        }
    }

    @GetMapping("/login/oauth2/code/{provider}")
    public String login(@RequestParam("code") String code, @PathVariable("provider") String provider) {
        if(provider.equals("google")){
            return googleService.login(code);
        }
        else if(provider.equals("kakao")){
            return kakaoService.login(code);
        }
        else return null; //이건 일반 로그인 사용하면 될듯. 컨트롤러는 UsersController에서 하나로 통합. GoogleController랑 Kakaocontroller는 필요없음.
    }
}