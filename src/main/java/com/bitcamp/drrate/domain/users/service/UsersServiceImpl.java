package com.bitcamp.drrate.domain.users.service;


import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.entity.Role;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo) {
        //이메일로 기존 사용자 조회
        Optional<Users> existUsers = usersRepository.findByEmail(userInfo.getKakaoAccount().getEmail());

        if (existUsers.isPresent()) {
            //기존 사용자 로그인 처리
            return existUsers.get();
        }
        // 신규 사용자 회원가입 처리
        Users newUsers = Users.builder()
                .email(userInfo.getKakaoAccount().getEmail())
                .username(userInfo.getKakaoAccount().getProfile().getNickName())
                .build();

        return usersRepository.save(newUsers);
    }


    @Override
    public void joinProc(UsersJoinDTO joinDTO) {
        String nickname = joinDTO.getNickname();
        String userId = joinDTO.getUserId();
        Role role = joinDTO.getRole();
        String password = joinDTO.getPassword();
        String email = joinDTO.getEmail();

        Boolean isExist = usersRepository.existsByEmail(email);

        if(isExist) {
            return;
        }

        Users users = new Users();

        users.setUsername(nickname);
        users.setEmail(email);
        users.setRole(Role.USER);
        users.setPassword(bCryptPasswordEncoder.encode(password));
        users.setUserId(userId);

        usersRepository.save(users);
    }

    @Override // 소셜로그인으로 로그인 시 Header에 AccessToken, Cookie에 RefreshToken 저장
    public HttpHeaders tokenSetting(Map<String, String> map) {
        HttpHeaders headers = new HttpHeaders();

        String access = map.get("access");
        String refresh = map.get("refresh");

        headers.set("Authorization", "Bearer " + access);
        // ResponseCookie cookie = ResponseCookie.from("refresh", refresh)
        //         .httpOnly(true)
        //         .sameSite("None")
        //         .secure(true)
        //         .maxAge(24*60*60)
        //         .path("/")
        //         .build();

        // headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
