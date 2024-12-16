package com.bitcamp.drrate.domain.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UsersRepository usersRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            //클라이언트 요청에서 userId, password 추출
            String userId = obtainUsername(request); // 클라이언트 요청의 키값으로 username이라는 이름으로 주어야한다
            String password = obtainPassword(request);

            System.out.println("userId = " + userId + "\n" + "password" + password);

            if (userId == null || password == null) {
                throw new UsersServiceExceptionHandler(ErrorStatus.USER_LOGIN_ERROR);
            }

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
        // 예외 발생 시 상세 로깅 추가 (선택 사항)
        throw new UsersServiceExceptionHandler(ErrorStatus.USER_AUTHENTICATION_FAILED);
    }
    }
    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();

            String userId = customUserDetails.getUserId();

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();
            String role = auth.getAuthority();

            Users users = usersRepository.findByUserId(userId);
            if (users == null) {
                throw new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND); // 사용자를 찾을 수 없음
            }
            Long id = users.getId();

            String access = jwtUtil.createJwt(id, "access", role, 600000L);
            String refresh = jwtUtil.createJwt(id, "refresh", role, 86400000L);

            //Refresh 토큰 저장
            refreshTokenService.saveTokens(String.valueOf(id), access, refresh);

            response.setHeader("Authorization", "Bearer " + access); // 액세스 토큰 헤더에 저장 (로컬스토리지 저장)
            response.setStatus(HttpStatus.OK.value());
            
        } catch (Exception e) {
            // 예외 발생 시 실패 응답 처리
            System.err.println("Successful authentication failed: " + e.getMessage());
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

	//로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        try {
            // 클라이언트에 인증 실패 응답
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.\"}");
        } catch (IOException e) {
            // 응답 처리 중 예외 발생 시 로깅 및 커스텀 예외
            System.err.println("인증객체 생성 실패 : " + e.getMessage());
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR); // 내부 서버 오류
        }
    }
}
