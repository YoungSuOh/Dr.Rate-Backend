package com.bitcamp.drrate.domain.users.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final UsersRepository usersRepository;

    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Value("${spring.mail.auth-code-expiration-millis}") // 이메일 인증 키 만료시간
    private long authCodeExpirationMillis;

    //이메일 전송
    @Override
    public void sendEmail(String toEmail, //이메인 인증 요청시 입력한 이메일
                          String title, // 관리자가 설정한 제목
                          String text) { // 이메일 내용
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text); // 폼생성 메서드 실행(1)
        try {
            emailSender.send(emailForm); // 이메일 발송(2)
        } catch (RuntimeException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.UNABLE_TO_SEND_EMAIL);
        }
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail,
                                             String title,
                                             String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
    //이메일 발송 메서드에 전달 및 레디스 저장
    @Override
    public void sendCodeToEmail(String toEmail) {
        try {
            // 이메일 중복 체크 (2-1)
            checkDuplicatedEmail(toEmail);

            String title = "Dr.Rate 이메일 인증 번호";
            String authCode = createCode(); // 인증번호 생성 (2-2)

            // 이메일 발송
            sendEmail(toEmail, title, authCode);

            // Redis에 인증번호 저장 (2-3)
            saveAuthEmail(toEmail, authCode, authCodeExpirationMillis);
            System.out.println("이메일 redis 저장완료");

        } catch (DuplicateKeyException e) {
            // 중복된 이메일 예외 처리
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_EMAIL_DUPLICATE);
        } catch (RedisException ea) {
            // Redis 저장 실패 예외 처리
            throw new UsersServiceExceptionHandler(ErrorStatus.REDIS_SAVE_FAILED);
        } catch (Exception eb) {
            // 기타 예외 처리
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 중복 이메일 체크
    private void checkDuplicatedEmail(String email) {
        if (usersRepository.findByEmail(email).isPresent()) {
            System.out.println("이미 가입된 이메일");
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_EMAIL_DUPLICATE);
        }
    }
    // 인증번호 생성
    private String createCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.MAIL_CREATE_FAILED);
        }
    }
    // 만들어준 인증코드와 사용자가 보낸 인증코드가 일치하는지 확인
    @Override
    public boolean verifiedCode(String email, String authCode) {
        // redis에서 key 값 조회해서 value값 가져오기기
        String redisAuthCode = getAuthEmailValue(email); 
         // redis에서 값이 있는지 조회, 사용자가 보낸 인증코드와 조회한 value값이 맞는지 확인
        //boolean authResult = checkExistsValue(email) && redisAuthCode.equals(authCode); 리턴값
        // 값이 있고 value와 보내준 코드가 맞으면 true 반환 아니면 false 반환
        return checkExistsValue(email) && redisAuthCode.equals(authCode);
    }

    // ---- redis -----
    private void saveAuthEmail(String email, String authCode, long expired) {
        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + email, authCode);
        redisTemplate.expire(email, expired, TimeUnit.SECONDS); // 만료 시간 설정
    }
    private String getAuthEmailValue(String email) {
        return (String) redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
    }
    private boolean checkExistsValue(String email) {
        String key = AUTH_CODE_PREFIX + email;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists; // RedisTemplate.hasKey()는 Null을 반환할 가능성이 있으므로 Null 체크 필요
    }
}
