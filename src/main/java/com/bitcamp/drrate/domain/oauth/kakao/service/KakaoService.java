package com.bitcamp.drrate.domain.oauth.kakao.service;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public interface KakaoService {
    
    public void loginKakao(HttpServletResponse response) throws IOException;

    public Map<String, String> login(String code);
}
