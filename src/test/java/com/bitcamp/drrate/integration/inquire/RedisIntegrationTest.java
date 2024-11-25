package com.bitcamp.drrate.integration.inquire;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedisIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redisShouldStoreMessage() {
        // Given
        String key = "testKey";
        String value = "testValue";

        // When : RedisTemplate을 사용하여 데이터를 Redis에 저장
        redisTemplate.opsForValue().set(key, value);

        // Then: Redis에서 데이터를 다시 가져와 저장된 값과 비교
        String retrievedValue = (String) redisTemplate.opsForValue().get(key);

        // 검증: 저장된 값과 가져온 값이 일치하는지 확인
        assertEquals(value, retrievedValue);
    }
}
