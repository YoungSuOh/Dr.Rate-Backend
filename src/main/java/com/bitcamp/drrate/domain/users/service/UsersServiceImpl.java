package com.bitcamp.drrate.domain.users.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;
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
        System.out.println("userId = " + userId);
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
        Optional<Users> existUsers = usersRepository.findByEmail(userInfo.getKakao_account().getEmail());

        if (existUsers.isPresent()) {
            //기존 사용자 로그인 처리
            return existUsers.get();
        }
        // 신규 사용자 회원가입 처리
        Users newUsers = Users.builder()
                .email(userInfo.getKakao_account().getEmail())
                .username(userInfo.getKakao_account().getProfile().getNickName())
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
        // 엔티티 매핑 상태 확인
        System.out.println("Mapped User Entity: " + newUser);
        usersRepository.save(newUser);
    }

    @Override
    public Users getMyInfo(Long id) {
        Users users = usersRepository.findUsersById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + id));
        return users;
    }

    @Override
    public String invalidAccessToken(String invalidAccessToken) {
        try {
            invalidAccessToken = invalidAccessToken.substring(7); // Remove "Bearer " prefix

            Long id = jwtUtil.getIdWithoutValidation(invalidAccessToken); // 만료된 토큰에서 사용자 id값 추출

            Users users = usersRepository.findUsersById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + id)); // 사용자 pk id 값으로 DB조회
            System.out.println("users = " + users);

            Role role = users.getRole(); // 유저 권한 추출
            String redisAccessToken = refreshTokenService.getAccessToken(String.valueOf(id)); //redis에 user pk id 값으로 access토큰 조회
            String access = "";
            if(redisAccessToken.equals(invalidAccessToken)) { // 유저가 보낸 만료된 access토큰과 redis에 있는 access 토큰 비교 둘이 같으면
                String refreshToken = refreshTokenService.getRefreshToken(String.valueOf(id)); // redis에 있는 refresh 토큰 가져옴
                boolean token = jwtUtil.isExpired(refreshToken); // refresh 토큰의 만료 여부 확인
                if(!token) { // refresh 토큰이 만료되지 않았으면 토큰 재발급 .. access refresh 둘다 재발급해서 redis에 저장
                    access = jwtUtil.createJwt(users.getId(), "access", "ROLE_" + String.valueOf(role), 86400000L); // 새로운 토큰 발급
                    String refresh = jwtUtil.createJwt(users.getId(), "refresh", "ROLE_" + String.valueOf(role), 86400000L); // 새로운 refresh 토큰 발급
                    refreshTokenService.saveTokens(String.valueOf(id), access, refresh); // redis에 새로운 access, refresh 토큰 저장
                }
                return access; // 새로운 access 토큰
            } else return access; // access = "";
        } catch(NumberFormatException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_FORMAT_ERROR);
        } catch(Exception ex) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void myInfoEdit(Users users) {
        try{
            String encodedPassword = bCryptPasswordEncoder.encode(users.getPassword());
            users.setPassword(encodedPassword);
            
            usersRepository.save(users);

        } catch(UsersServiceExceptionHandler e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void logout(CustomUserDetails userDetails) {
        try {
            String id = String.valueOf(userDetails.getId());

            refreshTokenService.deleteTokens(id);
            
        } catch(UsersServiceExceptionHandler ex) {
            throw new UsersServiceExceptionHandler(ErrorStatus.JSON_PROCESSING_ERROR);
        } catch(Exception e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override

    public void resetPassword(String userId, String newPassword) {
        Users users = usersRepository.findByUserId(userId);
        if (users == null) {
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND);
        }
        users.setPassword(bCryptPasswordEncoder.encode(newPassword));
        usersRepository.save(users);
    }

    @Override
    @Transactional
    public boolean deleteAccount(Long id, String password) {
        try {
            Users users = usersRepository.findUsersById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + id));
            String userPwd = users.getPassword();

            if ((password != null && bCryptPasswordEncoder.matches(password, userPwd)) || (password == null && userPwd == null && users.getSocial() != null)) {
                usersRepository.deleteById(id);
                refreshTokenService.deleteTokens(String.valueOf(id));
                return true;
            } else {
                System.out.println("조건 불충족: 비밀번호 불일치 또는 소셜 계정이 아님");
                return false;
            }
        } catch(UsersServiceExceptionHandler ex) {
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_AUTHENTICATION_FAILED);
        } catch(Exception e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
