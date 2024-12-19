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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final UsersRepository usersRepository;

    private static final String AUTH_CODE_PREFIX = "AuthCode:";
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    // 이메일 전송
    @Override
    public void sendEmail(String toEmail, String title, String text) {
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            emailSender.send(emailForm);
            logger.info("이메일 전송 성공: {}", toEmail);
        } catch (RuntimeException e) {
            logger.error("이메일 전송 실패: {}", toEmail, e);
            throw new UsersServiceExceptionHandler(ErrorStatus.UNABLE_TO_SEND_EMAIL);
        }
    }

    // 이메일 발송 및 Redis 저장
    @Override
    public void sendCodeToEmail(String toEmail) {
        try {
            // 중복 이메일 체크
            checkDuplicatedEmail(toEmail);

            String title = "Dr.Rate 이메일 인증 번호";
            String authCode = createCode();

            // 이메일 전송
            sendEmail(toEmail, title, "인증 코드: " + authCode);

            // Redis에 인증번호 저장
            saveAuthEmail(toEmail, authCode, authCodeExpirationMillis);
            logger.info("이메일 인증 코드 Redis 저장 완료: {}", toEmail);
        } catch (DuplicateKeyException e) {
            logger.warn("중복된 이메일 요청: {}", toEmail);
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_EMAIL_DUPLICATE);
        } catch (RedisException e) {
            logger.error("Redis 저장 실패: {}", toEmail, e);
            throw new UsersServiceExceptionHandler(ErrorStatus.REDIS_SAVE_FAILED);
        } catch (Exception e) {
            logger.error("이메일 인증 코드 전송 중 오류 발생: {}", toEmail, e);
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 인증 코드 검증
    @Override
    public boolean verifiedCode(String email, String authCode) {
        String redisAuthCode = getAuthEmailValue(email);
        boolean isVerified = checkExistsValue(email) && redisAuthCode.equals(authCode);

        if (isVerified) {
            logger.info("이메일 인증 성공: {}", email);
        } else {
            logger.warn("이메일 인증 실패: {}, 입력된 코드: {}", email, authCode);
        }
        return isVerified;
    }

    // 중복 이메일 체크
    private void checkDuplicatedEmail(String email) {
        if (usersRepository.findByEmail(email).isPresent()) {
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_EMAIL_DUPLICATE);
        }
    }

    // 인증 코드 생성
    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("인증 코드 생성 실패", e);
            throw new UsersServiceExceptionHandler(ErrorStatus.MAIL_CREATE_FAILED);
        }
    }

    // Redis 인증 코드 저장
    private void saveAuthEmail(String email, String authCode, long expired) {
        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + email, authCode);
        redisTemplate.expire(AUTH_CODE_PREFIX + email, expired, TimeUnit.MILLISECONDS);
    }

    private String getAuthEmailValue(String email) {
        return (String) redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
    }

    private boolean checkExistsValue(String email) {
        String key = AUTH_CODE_PREFIX + email;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
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
