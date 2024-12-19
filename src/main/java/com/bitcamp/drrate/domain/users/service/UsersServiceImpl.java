package com.bitcamp.drrate.domain.users.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitcamp.drrate.domain.oauth.kakao.dto.response.KakaoUserInfoResponseDTO;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.dto.response.UsersResponseDTO;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    @Transactional
    public Long getUserId(CustomUserDetails user) {
        Long id = user.getId();
        Users users = usersRepository.findUsersById(id)
                .orElseThrow(() -> new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND));
        return users.getId();
    }

    @Override
    @Transactional
    public Role getUserRole(CustomUserDetails user) {
        Long id = user.getId();
        Users users = usersRepository.findUsersById(id)
                .orElseThrow(() -> new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND));
        return users.getRole();
    }

    @Override
    @Transactional
    public UsersResponseDTO.ChatRoomUserInfo getChatRoomUserInfo(Long userId) {
        Users users = usersRepository.findUsersById(userId)
                .orElseThrow(() -> new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND));
        return UsersResponseDTO.ChatRoomUserInfo.builder()
                .name(users.getUsername())
                .email(users.getEmail())
                .build();
    }

    @Override
    public Page<Users> getUsersList(int page, int size, String searchType, String keyword) {
        try {
            if (page < 0 || size <= 0) {
                throw new UsersServiceExceptionHandler(ErrorStatus.USER_LIST_BAD_REQUEST);
            }

            Pageable pageable = PageRequest.of(page, size);

            // 검색 조건 처리
            if (searchType != null && keyword != null) {
                if (searchType.equalsIgnoreCase("name")) {
                    return usersRepository.findByUsernameContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
                } else if (searchType.equalsIgnoreCase("email")) {
                    return usersRepository.findByEmailContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
                } else {
                    throw new UsersServiceExceptionHandler(ErrorStatus.USER_LIST_BAD_REQUEST);
                }
            } else {
                // 검색 조건이 없을 경우
                return usersRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } catch (IllegalArgumentException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_LIST_BAD_REQUEST);
        } catch (JpaSystemException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.MYSQL_LOAD_FAILED);
        } catch (Exception e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Users handleLoginOrSignup(KakaoUserInfoResponseDTO userInfo) {
        //이메일로 기존 사용자 조회
        Optional<Users> existUsers = usersRepository.findByEmail(userInfo.getKakaoAccount().getEmail());

        if (existUsers.isPresent()) {
            //기존 사용자 로그인 처리
            return existUsers.get();
        }
        // 신규 사용자 회원가입 처리
        Users newUsers = Users.builder()
                .email(userInfo.getKakaoAccount().getEmail())
                .username(userInfo.getKakaoAccount().getProfile().getNickName())
                .build();

        return usersRepository.save(newUsers);
    }
    @Override
    @Transactional
    public void signUp(UsersJoinDTO usersJoinDTO) {

        // 이메일 중복 체크
        if(usersRepository.existsByEmail(usersJoinDTO.getEmail())){
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_EMAIL_DUPLICATE);
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(usersJoinDTO.getPassword());

        // 새 사용자 객체 생성
        Users newUser = Users.builder()
                .userId(usersJoinDTO.getUserId())
                .email(usersJoinDTO.getEmail())
                .username(usersJoinDTO.getUsername())
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        usersRepository.save(newUser);
    }


    @Override // 소셜로그인으로 로그인 시 Header에 AccessToken 전달
    public HttpHeaders tokenSetting(String access) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + access);
        headers.add("Access-Control-Expose-Headers", "Authorization");
        return headers;
    }
}
