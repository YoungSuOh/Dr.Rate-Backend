package com.bitcamp.drrate.domain.users.service;

public interface EmailService {
    public void sendEmail(String toEmail, String title, String text);
    public void sendCodeToEmail(String email);
    public boolean verifiedCode(String email, String authCode);
}
