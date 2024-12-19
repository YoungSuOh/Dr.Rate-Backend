package com.bitcamp.drrate.domain.users.service;


import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import org.springframework.http.HttpHeaders;

import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import org.springframework.data.domain.Page;


public interface UsersService {
    Long getUserId(CustomUserDetails user);
    Role getUserRole(CustomUserDetails user);
    UsersResponseDTO.ChatRoomUserInfo getChatRoomUserInfo(Long userId);
    Page<Users>getUsersList(int page, int size, String searchType, String keyword);
    Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo);
    HttpHeaders tokenSetting(String access);
    void signUp(UsersJoinDTO usersJoinDTO);
    Users getMyInfo(Long id);
}
