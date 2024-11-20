package com.bitcamp.drrate.domain.kakao.service;

import com.bitcamp.drrate.domain.kakao.dto.response.KakaoTokenResponseDTO;
import com.bitcamp.drrate.domain.kakao.dto.response.KakaoUserInfoResponseDTO;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private String client_id;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    @Autowired
    public KakaoService(@Value("${spring.security.oauth2.client.registration.kakao.client_id}") String client_id) {
        this.client_id = client_id;
        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }
    //Access토큰 발급
    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDTO kakaoTokenResponseDTO = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", client_id)
                        .queryParam("code",code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invaild Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        log.info("[Kakao Service] Access token ------> {}", kakaoTokenResponseDTO.getAccessToken());
        log.info("[Kakao Service] Refresh token ------> {}", kakaoTokenResponseDTO.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDTO.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDTO.getScope());

        return kakaoTokenResponseDTO.getAccessToken();
    }
    //사용자 정보 요청
    public KakaoUserInfoResponseDTO getUserInfo(String accessToken) {
        KakaoUserInfoResponseDTO userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) //access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDTO.class)
                .block();

        log.info("[Kakao Service] Auth ID ---> {} ", userInfo.getId());
        log.info("[Kakao Service] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[Kakao Service] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }
}
