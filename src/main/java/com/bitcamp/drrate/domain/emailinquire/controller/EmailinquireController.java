package com.bitcamp.drrate.domain.emailinquire.controller;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.bitcamp.drrate.domain.emailinquire.service.EmailinquireService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/emailinquire")
@RequiredArgsConstructor
public class EmailinquireController {
    private final EmailinquireService emailinquireService;
    
    // 이메일 문의 저장
    @RequestMapping(value="/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveEmailInquire(@RequestBody Emailinquire emailInquire, @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("이메일 문의 저장 메서드 실행");
        System.out.println("카테고리 = " + emailInquire.getInquireCtg() + "\n" +
                            "문의자 이름 = " + emailInquire.getInquireUser() + "\n" +
                            "문의자 이메일 = " + emailInquire.getInquireEmail() + "\n" + 
                            "문의 제목 = " + emailInquire.getInquireTitle() + "\n" +
                            "문의 내용 = " + emailInquire.getInquireContent());

        emailInquire.setInquireId(userDetails.getId());
        emailinquireService.saveEmailInquire(emailInquire);
        return ResponseEntity.ok(emailInquire);
    }

    // 이메일 문의 내역 조회 (사용자 ID별)
    @RequestMapping(value="/myinquired", method=RequestMethod.GET)
    public ResponseEntity<List<Emailinquire>> getEmailInquiresByUserId(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = userDetails.getId(); // 사용자 users table의 pk값

        List<Emailinquire> emailInquires = emailinquireService.getEmailInquiresByUserId(id);
        return ResponseEntity.ok(emailInquires);
    }

    // 이메일 문의 삭제
    @RequestMapping(value="/delete", method=RequestMethod.GET)
    public ResponseEntity<Void> deleteEmailInquire(@PathVariable Long id) {
        emailinquireService.deleteEmailInquire(id);
        return ResponseEntity.noContent().build();
    }
}