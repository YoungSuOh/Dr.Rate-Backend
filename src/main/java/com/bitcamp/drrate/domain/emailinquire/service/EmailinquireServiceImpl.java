package com.bitcamp.drrate.domain.emailinquire.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bitcamp.drrate.domain.emailinquire.entity.Emailinquire;
import com.bitcamp.drrate.domain.emailinquire.repository.EmailinquireRepository;
import com.bitcamp.drrate.domain.s3.service.S3Service;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
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
            helper.setText(emailInquire.getInquireEmail() + "\n\n" + emailInquire.getInquireContent(), false); // 내용 (HTML 여부는 false로 설정)

            // 문의자 이메일 설정
            helper.setFrom(emailInquire.getInquireEmail()); // Gmail은 보안정책 때문에 발신자 이메일을 변경할 수 없음.
            // 파일 첨부 처리
            if (fileUuid != null && !fileUuid.isEmpty()) {
                // 이메일 본문에 URL 추가
                helper.addAttachment(fileUuid.getOriginalFilename(), fileUuid);
                emailSender.send(message); // 문의 이메일 전송

                String fileUrl = s3Service.uploadFile(fileUuid); // 파일을 S3에 업로드
                emailInquire.setFileUuid(fileUrl);
                
                emailinquireRepository.save(emailInquire);
            } else {
                emailSender.send(message); // 문의 이메일 전송
                emailinquireRepository.save(emailInquire);
            }
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
    // 관리자 페이지 문의 내역 확인
    @Override
    public Page<Emailinquire> getEmailInquireList(Pageable pageable) {
        try {
            return emailinquireRepository.findAll(pageable);
        } catch(Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INQUIRE_LIST_GET_FAILED);
        }
        
    }

    @Override
    public void saveAnswerEmail(Long id, String answerTitle, String answerContent, MultipartFile answerFile) {
        try {
            Emailinquire emailDTO = emailinquireRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found for ID: " + id));
            // MIME 형식 이메일 생성
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 수신자, 제목, 본문 설정
            helper.setTo(emailDTO.getInquireEmail()); // 수신자 이메일
            helper.setSubject(answerTitle); // 제목
            helper.setText("보낸사람 : anfto023@gmail.com\n\n" + answerContent, false); // 내용 (HTML 여부는 false로 설정)

            emailDTO.setAnswerTitle(answerTitle);
            emailDTO.setAnswerContent(answerContent);

            // 파일 첨부 처리
            if (answerFile != null && !answerFile.isEmpty()) {
                // 이메일 본문에 URL 추가
                helper.addAttachment(answerFile.getOriginalFilename(), answerFile);
                emailSender.send(message); // 문의 이메일 전송

                String fileUrl = s3Service.uploadFile(answerFile); // 파일을 S3에 업로드
                emailDTO.setAnswerFile(fileUrl);
                
                emailinquireRepository.save(emailDTO);
            } else {
                emailSender.send(message); // 문의 이메일 전송
                emailinquireRepository.save(emailDTO);
            }
        } catch(RuntimeException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INQUIRE_LIST_GET_FAILED);
        } catch(Exception ex) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}