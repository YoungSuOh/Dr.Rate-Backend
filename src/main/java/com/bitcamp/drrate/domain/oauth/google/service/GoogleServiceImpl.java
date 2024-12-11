package com.bitcamp.drrate.domain.oauth.google.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.oauth.google.dto.response.GoogleUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO.GoogleUserInfo;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GoogleServiceImpl implements GoogleService {
    
    private final UsersRepository usersRepository;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;



    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirect_uri;

    @Override
    public void loginGoogle(HttpServletResponse response) throws IOException {
        //url에 get방식으로 인가코드를 요청한다
        String url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + client_id + 
                        "&redirect_uri=" + redirect_uri + 
                        "&response_type=code&scope=email profile";
        response.sendRedirect(url);
    }

    @Override
    public Map<String, String> login(String code) {
        //System.out.println("code = " + code);
        //코드를 받고 AccessToken을 받음. 아래에 getAccessToken 메서드를 통해서 accessToken을 발급받음
        String accessToken = getAccessToken(code);
        //System.out.println("AccessToken = " + accessToken);
        //발급받은 accessToken에서 값을 분리해서 access_token 값만 따로 파싱해서 가져옴
        GoogleUserInfoResponseDTO.UserAccessTokenDTO token = parseAccessToken(accessToken);

        // 파싱한 accessToken을 이용해서 사용자 정보 받아오기
        RestClient restClient = RestClient.create();
        ResponseEntity<String> result = restClient.post()
            //.uri("https://www.googleapis.com/userinfo/v2/me?access_token=" + token) 헤더에 토큰값을 주지 않고 사용했을 때
            .uri("https://www.googleapis.com/oauth2/v3/userinfo") //구글 사용자 정보 요청 주소임
            .headers(headersHttp -> headersHttp.setBearerAuth(token.getAccessToken())) //요청헤더값에 Authorization Bearer + token 을 넣었음. 헤더값에 넣는것이 보안에 유리
            .retrieve() //retrieve(). 응답 본문은 body(Class)또는 body(ParameterizedTypeReference)목록과 같은 매개변수화된 유형에 대해 사용하여 액세스할 수 있습니다. //공식문서 퍼옴
            .toEntity(String.class);

         String userInfo = result.getBody();
        //사용자 정보를 json형식으로 받아서 콘솔창으로 확인
        //System.out.println(result.getStatusCode() + "\n" + result.getHeaders() + "\n" + result.getBody());

        GoogleUserInfo googleInfo = new GoogleUserInfo();
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> parse = objectMapper.readValue(userInfo, Map.class);

            googleInfo.setEmail((String)parse.get("email"));
            googleInfo.setName((String)parse.get("name"));
            googleInfo.setSub((String)parse.get("sub"));
            googleInfo.setPicture((String)parse.get("picture"));
            
            //소셜로그인으로 들어올 시 해당하는 소셜의 정보가 바뀔 수 있기 때문에 업데이트를 계속 해주어야한다.
            String email = googleInfo.getEmail();

            Optional<Users> optionalUsers = usersRepository.findByEmail(email);

            Users users = optionalUsers.orElseGet(() -> new Users());
            
            setUserInfo(users, googleInfo);

            /* 로그인 로직 수정 부탁드립니다 :) */
            usersRepository.save(users);

            // UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(googleInfo.getEmail(), null, null);
            String access = jwtUtil.createJwt("access", email, "ROLE_USER", 600000L);
            String refresh = jwtUtil.createJwt("refresh", email, "ROLE_USER", 86400000L);

            /* 우리 서버 token 값 */
            System.out.println("우리 서버 accessToken :  "+access);
            System.out.println("우리 서버 refreshToken :  "+refresh);

            /* 로그인 후 Redis에 access, refresh */
            refreshTokenService.saveTokens(String.valueOf(users.getId()), access, refresh);

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
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //accessToken 반환
    private String getAccessToken(String code) {
        //RestTemplate는 웹 서버에 Http 요청을 보내고 응답을 받기위한 메서드
        RestTemplate restTemplate = new RestTemplate();
        //구글의 AccessToekn을 받기위한 요청 주소
        String url = "https://oauth2.googleapis.com/token";
        // Http 요청 헤더를 성정하는 메서드
        HttpHeaders headers = new HttpHeaders();
        // Http ContentType을 설정하는줄
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //System.out.println("Header: " + headers.toString()); //헤더 확인

        // Http Body를 만들기 위해서 Map에 여러 변수를 담는 줄
        // 구글 API를 사용하기 위해 필요한 값들을 담기 위해서 사용
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("redirect_uri", redirect_uri);
        params.add("grant_type", "authorization_code");
        //System.out.println("Parameters: " + params.toString()); // Map 확인

        // Http 요청의 Body부분, 요청을 위해 header와 map에 담은 값들을 담아주는 줄
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        //System.out.println("Request Body: " + request.getBody()); // HttpEntity의 바디 확인
        //System.out.println("Request Headers: " + request.getHeaders()); // HttpEntity의 헤더 확인

        // Http 응답을 나타내는 클래스, 응답 형태를 String 형태로 받기위해 사용.
        // restTemplate.postForEntity로 요청을 보내고 구글 API 서버로 부터 응답을 ResponseEntity 객체로 받아온다.
        // 이 객체로 Http 응답 코드, 헤더, 바디 등을 받아온다.
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        // 요청해서 받아왔으니 이제 sysout으로 받아온 값들을 확인해본다.
        //System.out.println("Response Body: " + response.getBody()); // ResponseEntity의 바디 확인
        //System.out.println("Response Status Code: " + response.getStatusCode()); // ResponseEntity의 상태 코드 확인
        
        //이제 이 코드가 위에login 메서드에서 사용됨
        return response.getBody();
    }

    // 받아온 accessToken 값을 파싱해서 내가 필요한 access_token값만 가져옴
    public GoogleUserInfoResponseDTO.UserAccessTokenDTO parseAccessToken(String accessToken) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> parseToken = objectMapper.readValue(accessToken, Map.class);
            
            GoogleUserInfoResponseDTO.UserAccessTokenDTO tokenDTO = new GoogleUserInfoResponseDTO.UserAccessTokenDTO();

            tokenDTO.setAccessToken((String)parseToken.get("access_token"));
            tokenDTO.setExpriesIn((Integer)parseToken.get("expires_in"));
            tokenDTO.setScope((String)parseToken.get("scope"));
            tokenDTO.setTokenType((String)parseToken.get("token_type"));
            tokenDTO.setIdToken((String)parseToken.get("id_token"));

            // System.out.println("access_token: " + token + "\n" + 
            //                     "expires_in: " + expiresIn + "\n" + 
            //                     "scope: " + scope + "\n" + 
            //                     "token_type: " + tokenType + "\n" +
            //                     "id_token: " + idToken);

            return tokenDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setUserInfo(Users users, GoogleUserInfo googleInfo) {
        users.setEmail(googleInfo.getEmail());
        users.setRole(Role.USER);
    }
}
