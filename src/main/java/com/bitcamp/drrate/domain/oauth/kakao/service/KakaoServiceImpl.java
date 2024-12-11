package com.bitcamp.drrate.domain.oauth.kakao.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoTokenResponseDTO;
import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;

import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoServiceImpl implements KakaoService {

    private final UsersRepository usersRepository;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String client_id;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirect_uri;

    @Override
    public void loginKakao(HttpServletResponse response) throws IOException {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id +"&redirect_uri=" + redirect_uri;

        response.sendRedirect(location);
    }

    @Override
    public Map<String, String> login(String code) {
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

        if(kakaoTokenResponseDTO == null) {
            log.error("[Kakao Service] Token response DTO is null");
            return null;
        }
        log.info("[Kakao Service] Access token ------> {}", kakaoTokenResponseDTO.getAccessToken());
        log.info("[Kakao Service] Refresh token ------> {}", kakaoTokenResponseDTO.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDTO.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDTO.getScope());

        KakaoUserInfoResponseDTO userInfo = getUserInfo(kakaoTokenResponseDTO.getAccessToken());

        //소셜로그인으로 들어올 시 해당하는 소셜의 정보가 바뀔 수 있기 때문에 업데이트를 계속 해주어야한다.
        String email = userInfo.getKakaoAccount().getEmail();

        System.out.println("email : "+email);
        Optional<Users> optionalUsers = usersRepository.findByEmail(email);

        Users users = optionalUsers.orElseGet(() -> new Users());

        setUserInfo(users, userInfo);

        usersRepository.save(users);
        
        // UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(googleInfo.getEmail(), null, null);
        String access = jwtUtil.createJwt("access", email, "ROLE_USER", 600000L);
        String refresh = jwtUtil.createJwt("refresh", email, "ROLE_USER", 86400000L);

        /* 로그인 후 Redis에 access, refresh */
        refreshTokenService.saveRefreshToken(access, refresh);

        //Refresh 토큰 DB에 저장
        // Date date = new Date(System.currentTimeMillis() + 86400000L);

        // RefreshEntity refreshEntity = new RefreshEntity();
        // refreshEntity.setUsername(email);
        // refreshEntity.setRefresh(refresh);
        // refreshEntity.setExpiration(date.toString());

        // refreshRepository.save(refreshEntity);

        Map<String, String> map = new HashMap<>();
        map.put("access", access);
        map.put("refresh", refresh);

        return map; 
    }

    //사용자 정보 요청
    private KakaoUserInfoResponseDTO getUserInfo(String accessToken) {
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

        if(userInfo == null) {
            log.error("[Kakao Service] UserInfo is null");
            return null;
        }
        log.info("[Kakao Service] Auth ID ---> {} ", userInfo.getId());
        log.info("[Kakao Service] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[Kakao Service] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    private void setUserInfo(Users users, KakaoUserInfoResponseDTO userInfo) {
        users.setEmail(userInfo.getKakaoAccount().getEmail());
        users.setUsername(userInfo.getKakaoAccount().getName());
        users.setRole(Role.USER);
    }
}
