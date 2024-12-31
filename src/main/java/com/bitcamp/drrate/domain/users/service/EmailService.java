package com.bitcamp.drrate.domain.users.service;

public interface EmailService {
    public void sendEmail(String toEmail, String title, String text);

    // 이메일로 가입된 아이디 전송
    public void sendIdToEmail(String toEmail);
    public void sendCodeToEmail(String email);
    public boolean verifiedCode(String email, String authCode);
}
