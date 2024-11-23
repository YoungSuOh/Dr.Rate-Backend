package com.bitcamp.drrate.domain.kakao.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public interface KakaoService {
    
    public void loginKakao(HttpServletResponse response) throws IOException;

    public String login(String code);
}
