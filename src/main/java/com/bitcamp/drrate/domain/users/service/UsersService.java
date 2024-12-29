package com.bitcamp.drrate.domain.users.service;


import org.springframework.data.domain.Page;

import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;


public interface UsersService {
    Long getUserId(CustomUserDetails user);
    Role getUserRole(CustomUserDetails user);
    UsersResponseDTO.ChatRoomUserInfo getChatRoomUserInfo(Long userId);
    Page<Users>getUsersList(int page, int size, String searchType, String keyword);
    Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo);
    void signUp(UsersJoinDTO usersJoinDTO);
    Users getMyInfo(Long id);
    String invalidAccessToken(String accessToken);
    void myInfoEdit(Users users);
    void logout(CustomUserDetails userDetails);
    void deleteAccount(Long id, String password);
}
