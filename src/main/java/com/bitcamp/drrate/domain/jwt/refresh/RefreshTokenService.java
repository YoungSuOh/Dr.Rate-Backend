package com.bitcamp.drrate.domain.jwt.refresh;

public interface RefreshTokenService {
    // Refresh Token 저장
    public void saveRefreshToken(String accessToken, String refreshToken);

    // Refresh Token 조회
    public String getRefreshToken(String accessToken);

    // Refresh Token 삭제
    public void deleteRefreshToken(String accessToken);
}
