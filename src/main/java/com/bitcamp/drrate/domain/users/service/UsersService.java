package com.bitcamp.drrate.domain.users.service;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import org.springframework.http.HttpHeaders;

import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.entity.Users;

public interface UsersService {
    Long getUserId(CustomUserDetails user);
    Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo);
    void joinProc(UsersJoinDTO joinDTO);
    HttpHeaders tokenSetting(String access);
}
