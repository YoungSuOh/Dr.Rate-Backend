package com.bitcamp.drrate.domain.users.service;


import com.bitcamp.drrate.domain.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Users;

public interface UsersService {
    Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo);
}
