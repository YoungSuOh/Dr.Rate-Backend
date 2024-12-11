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
    public void saveRefreshToken(String accessToken, String refreshToken) {
        String key = String.valueOf(accessToken);
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpiry, TimeUnit.SECONDS);
    }

    @Override
    public String getRefreshToken(String accessToken) {
        String key = String.valueOf(accessToken);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRefreshToken(String accessToken) {
        String key = String.valueOf(accessToken);
        redisTemplate.delete(key);
    }
}