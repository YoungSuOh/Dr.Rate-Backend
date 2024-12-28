package com.bitcamp.drrate.domain.emailinquire.service;

import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;
import com.bitcamp.drrate.domain.emailinquire.repository.EmailinquireRepository;
import com.bitcamp.drrate.domain.s3.service.S3Service;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailinquireServiceImpl implements EmailinquireService {

    private final EmailinquireRepository emailinquireRepository;
    private final JavaMailSender emailSender;
    private final S3Service s3Service;

    // 문의 저장
    @Override
    public void saveEmailInquire(Emailinquire emailInquire, MultipartFile fileUuid) {
        try {
            // MIME 형식 이메일 생성
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 수신자, 제목, 본문 설정
            helper.setTo("anfto023@gmail.com"); // 수신자 이메일
            helper.setSubject(emailInquire.getInquireTitle()); // 제목
            helper.setText(emailInquire.getInquireContent(), false); // 내용 (HTML 여부는 false로 설정)

            // 문의자 이메일 설정
            helper.setFrom(emailInquire.getInquireEmail()); // Gmail은 보안정책 때문에 발신자 이메일을 변경할 수 없음.
            helper.setText(emailInquire.getInquireEmail(), false);

            // 파일 첨부 처리
            if (fileUuid != null && !fileUuid.isEmpty()) {
                helper.addAttachment(fileUuid.getOriginalFilename(), fileUuid);
                String fileUrl = s3Service.uploadFile(fileUuid); // 클라우드 저장 및 파일 url
                emailInquire.setFileUuid(fileUrl);
            }

            emailSender.send(message); // 문의 이메일 전송

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





}