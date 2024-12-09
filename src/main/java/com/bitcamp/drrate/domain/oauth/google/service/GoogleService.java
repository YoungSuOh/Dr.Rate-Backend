package com.bitcamp.drrate.domain.oauth.google.service;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public interface GoogleService {

    public void loginGoogle(HttpServletResponse response) throws IOException;

    public Map<String, String> login(String code);

}
