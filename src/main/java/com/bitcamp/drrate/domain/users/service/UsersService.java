package com.bitcamp.drrate.domain.users.service;


import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

public interface UsersService {
    Long getUserId(CustomUserDetails user);
    Role getUserRole(CustomUserDetails user);
    UsersResponseDTO.ChatRoomUserInfo getChatRoomUserInfo(Long userId);
    Page<Users>getUsersList(int page, int size);
    Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo);
    void joinProc(UsersJoinDTO joinDTO);
    HttpHeaders tokenSetting(String access);
}
