package com.bitcamp.drrate.domain.jwt.refresh;

public interface RefreshTokenService {
    // Access Token과 Refresh Token 저장
    void saveTokens(String userId, String accessToken, String refreshToken);

    // Access Token 조회
    String getAccessToken(String userId);

    // Refresh Token 조회
    String getRefreshToken(String userId);

    // Access Token과 Refresh Token 삭제
    void deleteTokens(String userId);
}