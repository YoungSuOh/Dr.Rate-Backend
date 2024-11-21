package com.bitcamp.drrate.domain.google.service;

import java.io.IOException;

import com.bitcamp.drrate.domain.google.dto.GoogleUserInfoResponseDTO.UserInfoDTO;

import jakarta.servlet.http.HttpServletResponse;

public interface GoogleService {

    public void loginGoogle(HttpServletResponse response) throws IOException;

    public UserInfoDTO login(String code);

}
