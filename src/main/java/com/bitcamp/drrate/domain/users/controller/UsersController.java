package com.bitcamp.drrate.domain.users.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.drrate.domain.oauth.google.dto.response.GoogleUserInfoResponseDTO.UserInfoDTO;
import com.bitcamp.drrate.domain.oauth.google.service.GoogleService;
import com.bitcamp.drrate.domain.oauth.kakao.service.KakaoService;
import com.bitcamp.drrate.domain.users.jwt.JWTUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
            UserInfoDTO googleUser = googleService.login(code);
            return "";
        }
        else if(provider.equals("kakao")){
            return kakaoService.login(code);
        }
        else return "";
    }

    @GetMapping("/customLogin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> customLogin(Model model, HttpServletRequest request) {
        String user_name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("사용자 이름 = " + user_name);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication = " + authentication);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();

        String role = auth.getAuthority();
        System.out.println("role = " + role);
        
        String access = request.getHeader("access"); //access
        System.out.println("access Token = " + access);

        String refresh = null; // refresh
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }
        System.out.println("refresh Token = " + refresh);
        
        // model.addAttribute("accessToken", access);
        // model.addAttribute("refreshToken", refresh);
        // model.addAttribute("username", user_name);
        // model.addAttribute("role", role);

        Map<String, Object> map = new HashMap<>();

        map.put("accessToken", access);
        map.put("refreshToken", refresh);
        map.put("username", user_name);
        map.put("role", role);
        return ResponseEntity.ok(map);
    }
}