package com.bitcamp.drrate.domain.emailinquire.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;

public interface EmailinquireService {
    void saveEmailInquire(Emailinquire emailInquire, MultipartFile fileUuid);
    List<Emailinquire> getEmailInquiresByUserId(Long userId);
    void deleteEmailInquire(Long id);
    Page<Emailinquire> getEmailInquireList(Pageable pageable);
    void saveAnswerEmail(Long id, String answerTitle, String answerContent, MultipartFile answerFile);
}