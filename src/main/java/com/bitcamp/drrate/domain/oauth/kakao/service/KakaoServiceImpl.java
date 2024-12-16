package com.bitcamp.drrate.domain.oauth.kakao.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
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

import static com.bitcamp.drrate.domain.users.entity.Role.ADMIN;

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
    public String login(String code) {
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


        KakaoUserInfoResponseDTO userInfo = getUserInfo(kakaoTokenResponseDTO.getAccessToken());

        //소셜로그인으로 들어올 시 해당하는 소셜의 정보가 바뀔 수 있기 때문에 업데이트를 계속 해주어야한다.
        String email = userInfo.getKakaoAccount().getEmail();

        Optional<Users> optionalUsers = usersRepository.findByEmail(email);

        Users users = optionalUsers.orElseGet(() -> new Users());
        
        setUserInfo(users, userInfo);

        Long id = users.getId();

        usersRepository.save(users);

        String access = null; String refresh = null;

        System.out.println("role : "+users.getRole());
        if(users.getRole().equals(ADMIN)){
            access = jwtUtil.createJwt(id, "access", "ROLE_ADMIN", 86400000L);
            refresh = jwtUtil.createJwt(id, "refresh", "ROLE_ADMIN", 86400000L);
        }else{
            access = jwtUtil.createJwt(id, "access", "ROLE_USER", 86400000L);
            refresh = jwtUtil.createJwt(id, "refresh", "ROLE_USER", 86400000L);
        }

        /* 우리 서버 token 값 */
        System.out.println("우리 서버 accessToken :  "+access);
        System.out.println("우리 서버 refreshToken :  "+refresh);

        /* 로그인 후 Redis에 access, refresh */
        refreshTokenService.saveTokens(String.valueOf(users.getId()), access, refresh);

        System.out.println(refreshTokenService.getAccessToken(String.valueOf(users.getId())));
        System.out.println(refreshTokenService.getRefreshToken(String.valueOf(users.getId())));

        return access;
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

        return userInfo;
    }

    private void setUserInfo(Users users, KakaoUserInfoResponseDTO userInfo) {
        users.setEmail(userInfo.getKakaoAccount().getEmail());
        users.setUsername(userInfo.getKakaoAccount().getName());
        if(userInfo.getKakaoAccount().getName() == null){
            users.setUsername(userInfo.getKakaoAccount().getProfile().getNickName());
        }
        if(users.getRole().equals("ADMIN")){
            users.setRole(Role.ADMIN);
        }else{
            users.setRole(Role.USER);
        }
    }
}
