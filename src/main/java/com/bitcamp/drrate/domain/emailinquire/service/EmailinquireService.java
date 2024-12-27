package com.bitcamp.drrate.domain.emailinquire.service;

import java.util.List;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;

public interface EmailinquireService {
    void saveEmailInquire(Emailinquire emailInquire);
    List<Emailinquire> getEmailInquiresByUserId(Long userId);
    void deleteEmailInquire(Long id);
}