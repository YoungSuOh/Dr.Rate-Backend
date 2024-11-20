package com.bitcamp.drrate.domain.kakao.controller;

import com.bitcamp.drrate.domain.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.kakao.service.KakaoService;
import com.bitcamp.drrate.domain.kakao.service.KakaoServiceImpl;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {
    private final KakaoService kakaoService;
    private final UsersService usersService;
    @Value("${spring.security.oauth2.client.registration.kakao.client_id}")
    private String client_id;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/page")
    public String loginPage(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id +"&redirect_uri=" + redirect_uri;
        model.addAttribute("location", location);

        return "login";
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code)throws IOException {
        //Access Token 획득
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        //사용자 정보 가져오기
        KakaoUserInfoResponseDTO userInfo = kakaoService.getUserInfo(accessToken);

        //User 로그인, 또는 회원가입 로직 추가
        Users users = usersService.handleLoginOrSignup(userInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
