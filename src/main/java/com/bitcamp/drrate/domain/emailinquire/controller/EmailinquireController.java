package com.bitcamp.drrate.domain.emailinquire.controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;
import com.bitcamp.drrate.domain.emailinquire.service.EmailinquireService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/emailinquire", "/api/inquiries"})
@RequiredArgsConstructor
public class EmailinquireController {
    private final EmailinquireService emailinquireService;
    
    // 이메일 문의 저장
    @RequestMapping(value="/save", method=RequestMethod.POST, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> saveEmailInquire(@RequestParam("inquireCtg") String inquireCtg,
                                                @RequestParam("inquireUser") String inquireUser,
                                                @RequestParam("inquireEmail") String inquireEmail,
                                                @RequestParam("inquireTitle") String inquireTitle,
                                                @RequestParam("inquireContent") String inquireContent,
                                                @RequestParam(value="fileUuid", required=false) MultipartFile fileUuid,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        try {
            Emailinquire emailInquire = new Emailinquire();

            emailInquire.setInquireCtg(inquireCtg);
            emailInquire.setInquireUser(inquireUser);
            emailInquire.setInquireEmail(inquireEmail);
            emailInquire.setInquireTitle(inquireTitle);
            emailInquire.setInquireContent(inquireContent);
            emailInquire.setInquireId(userDetails.getId());
            
            emailinquireService.saveEmailInquire(emailInquire, fileUuid);
            return ApiResponse.onSuccess(true, SuccessStatus.INQUIRE_LIST_GET_SUCCESS);
        } catch(Exception e) {
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_LIST_GET_FAILED.getCode(), ErrorStatus.INQUIRE_LIST_GET_FAILED.getMessage(), null);
        }
    }

    // 이메일 문의 내역 조회 (사용자 ID별)
    @GetMapping
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

    //관리자 이메일 문의 내역 리스트 페이지
    @RequestMapping(value="/emailInquireList", method=RequestMethod.GET)
    public ApiResponse<?> getEmailInquireList(@RequestParam(name="page", defaultValue = "0") int page,
                                                @RequestParam(name="size", defaultValue = "5") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            Page<Emailinquire> emailPage = emailinquireService.getEmailInquireList(pageable);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 데이터를 변환하여 포맷팅
            List<Map<String, Object>> formattedContent = emailPage.getContent().stream().map(email -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", email.getId());
                map.put("inquireCtg", email.getInquireCtg());
                map.put("inquireEmail", email.getInquireEmail());
                map.put("inquireTitle", email.getInquireTitle());
                map.put("inquireContent", email.getInquireContent());
                map.put("fileUuid", email.getFileUuid());
                map.put("inquireUser", email.getInquireUser());
                map.put("answerContent", email.getAnswerContent());
                map.put("createdAt", email.getCreatedAt().format(formatter)); // 포맷 적용
                return map;
            }).toList();

            Map<String, Object> result = new HashMap<>();
            result.put("content", formattedContent); // 포맷팅된 데이터 리스트
            result.put("totalPages", emailPage.getTotalPages());
            result.put("currentPage", emailPage.getNumber());
            result.put("totalItems", emailPage.getTotalElements());

            return ApiResponse.onSuccess(result, SuccessStatus.INQUIRE_LIST_GET_SUCCESS); 
        } catch(Exception e) {
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_LIST_GET_FAILED.getCode(), ErrorStatus.INQUIRE_LIST_GET_FAILED.getMessage(), null);
        }
    }

    //관리자 이메일 답변 전송
    @RequestMapping(value="/answer", method=RequestMethod.POST)
    public ApiResponse<?> answerEmail(@RequestParam("id") Long id,
                                        @RequestParam("answerTitle") String answerTitle,
                                        @RequestParam("answerContent") String answerContent,
                                        @RequestParam(value="answerFile", required=false) MultipartFile answerFile) {
        try {

            emailinquireService.saveAnswerEmail(id, answerTitle, answerContent, answerFile);

            return ApiResponse.onSuccess(true, SuccessStatus.USER_VERIFYCATION_EMAIL);
        } catch(Exception e) {
            return ApiResponse.onFailure(ErrorStatus.UNABLE_TO_SEND_EMAIL.getCode(), ErrorStatus.UNABLE_TO_SEND_EMAIL.getMessage(), null);
        }
        
    }
}