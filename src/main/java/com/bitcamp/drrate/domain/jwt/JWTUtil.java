package com.bitcamp.drrate.domain.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;


@Component
public class JWTUtil {
    
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
    
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Long getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(Long id, String category, String role, Long expiredMs) {
        return Jwts.builder()
            .claim("id", id)
            .claim("category", category)
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }

    public Long getIdWithoutValidation(String token) {
        if (token == null || token.isEmpty()) {
            System.out.println("토큰이 비어 있거나 null입니다: " + token);
            throw new IllegalArgumentException("JWT 토큰이 비어 있거나 null입니다.");
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey) // 서명 검증은 수행
                    .build()
                    .parseSignedClaims(token) // 토큰 파싱
                    .getPayload(); // 클레임 추출

            return claims.get("id", Long.class);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 클레임 추출
            System.out.println("만료된 토큰에서 클레임 추출: " + e.getClaims());
            return e.getClaims().get("id", Long.class);
        }
    }
}
