package com.bitcamp.drrate.domain.kakao.service;

import com.bitcamp.drrate.domain.kakao.dto.response.KakaoUserInfoResponseDTO;

public interface KakaoService {
    String getAccessTokenFromKakao(String code);

    KakaoUserInfoResponseDTO getUserInfo(String accessToken);
}
