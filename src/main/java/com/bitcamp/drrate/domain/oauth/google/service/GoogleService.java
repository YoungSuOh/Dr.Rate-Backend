package com.bitcamp.drrate.domain.oauth.google.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public interface GoogleService {

    public void loginGoogle(HttpServletResponse response) throws IOException;

    public String login(String code);

}
