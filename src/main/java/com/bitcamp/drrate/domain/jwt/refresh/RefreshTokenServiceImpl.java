package com.bitcamp.drrate.domain.jwt.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Long refreshTokenExpiry = 2 * 7 * 24 * 60 * 60L; // 14일(2주)

    @Override
    public void saveTokens(String userId, String accessToken, String refreshToken) {
        redisTemplate.opsForHash().put(userId, "access", accessToken);
        redisTemplate.opsForHash().put(userId, "refresh", refreshToken);
        redisTemplate.expire(userId, refreshTokenExpiry, TimeUnit.SECONDS); // 만료 시간 설정
    }

    @Override
    public String getAccessToken(String userId) {
        return (String) redisTemplate.opsForHash().get(userId, "access");
    }

    @Override
    public String getRefreshToken(String userId) {
        return (String) redisTemplate.opsForHash().get(userId, "refresh");
    }

    @Override
    public void deleteTokens(String userId) {
        redisTemplate.delete(userId); // 전체 키 삭제
    }
}
