package com.bitcamp.drrate.domain.emailinquire.service;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;
import com.bitcamp.drrate.domain.emailinquire.repository.EmailinquireRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailinquireServiceImpl implements EmailinquireService {

    private final EmailinquireRepository emailinquireRepository;
    private final JavaMailSender emailSender;

    // 문의 저장
    @Override
    public void saveEmailInquire(Emailinquire emailInquire) {
        //이메일 폼 생성 (받는 사람, 문의 제목, 문의 내용)
        SimpleMailMessage emailForm = createEmailForm("anfto023@gmail.com", emailInquire.getInquireTitle(), emailInquire.getInquireContent());
        try {
            //
            emailForm.setFrom(emailInquire.getInquireEmail()); //문의자 이메일 설정
            emailSender.send(emailForm); // 문의 이메일 전송

            emailinquireRepository.save(emailInquire);
        } catch(RuntimeException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.UNABLE_TO_SEND_EMAIL);
        } catch(Exception e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 사용자 ID로 문의 내역 조회
    public List<Emailinquire> getEmailInquiresByUserId(Long userId) {
        return emailinquireRepository.findByInquireId(userId);
    }

    // 문의 삭제
    public void deleteEmailInquire(Long id) {
        emailinquireRepository.deleteById(id);
    }




    // 이메일 폼 생성
    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);
        return message;
    }
}