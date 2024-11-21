package com.bitcamp.drrate.domain.google.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitcamp.drrate.domain.google.dto.response.GoogleUserInfoResponseDTO.UserInfoDTO;
import com.bitcamp.drrate.domain.google.service.GoogleService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleService googleService;

    // 인가 코드 요청
    @GetMapping("/loginGoogle")
    public void loginGoogle(HttpServletResponse response) throws IOException {
        googleService.loginGoogle(response);
    }

    // 인가코드 요청 성공시 code값을 받고 getAccessToken을 사용해서 accesstoken 요청 후 사용자 정보 반환
    @GetMapping("/login/oauth2/code/google")
    public UserInfoDTO login(@RequestParam("code") String code) {
        return googleService.login(code);
    }

}
