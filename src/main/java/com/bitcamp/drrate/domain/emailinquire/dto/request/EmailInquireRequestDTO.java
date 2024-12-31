package com.bitcamp.drrate.domain.emailinquire.dto.request;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EmailInquireRequestDTO {
    private Long id; // 이메일 문의 고유 ID
    private String inquireCtg; // 이메일 문의 카테고리
    private Long inquireId; // 이메일 문의자 아이디(users 테이블의 id)
    private String inquireUser;
    private String inquireEmail; // 이메일 문의자 이메일
    private LocalDateTime createAt;
    private String answerContent; // 이메일 문의 답변 내용
}
