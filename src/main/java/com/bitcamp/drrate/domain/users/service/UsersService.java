package com.bitcamp.drrate.domain.users.service;


import java.util.Map;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import org.springframework.http.HttpHeaders;

import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsersService {
    Long getUserId(CustomUserDetails user);
    public UsersResponseDTO.ChatRoomUserInfo getChatRoomUserInfo(Long userId);
    Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo);
    void joinProc(UsersJoinDTO joinDTO);
    HttpHeaders tokenSetting(String access);
}
