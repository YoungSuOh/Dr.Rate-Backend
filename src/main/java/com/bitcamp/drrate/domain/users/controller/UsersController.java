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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.drrate.domain.oauth.google.service.GoogleService;
import com.bitcamp.drrate.domain.oauth.kakao.service.KakaoService;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.domain.users.service.UsersService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final UsersService usersService;
    /* 임시 */
    private final UsersRepository usersRepository;

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