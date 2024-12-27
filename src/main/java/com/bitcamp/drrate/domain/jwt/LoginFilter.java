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

    // --- [중요] 필터가 처리할 URL을 "/api/signIn"으로 지정 ---
    {
        this.setFilterProcessesUrl("/api/signIn");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        // 프론트엔드에서 username/password를 FormData (또는 x-www-form-urlencoded)로 전송
        String userId = obtainUsername(request);  // == request.getParameter("username")
        String password = obtainPassword(request); // == request.getParameter("password")

        System.out.println("userId = " + userId + "\n" + "password = " + password);

        if (userId == null || password == null) {
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_LOGIN_ERROR);
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userId, password);

        return authenticationManager.authenticate(authToken);

    }

    // 로그인 성공 시 JWT 발급
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String userId = customUserDetails.getUserId();

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();
            String role = auth.getAuthority();

            Users users = usersRepository.findByUserId(userId);
            if (users == null) {
                throw new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND);
            }
            Long id = users.getId();

            // Access 토큰 (10분), Refresh 토큰 (24시간)
            String access = jwtUtil.createJwt(id, "access", role, 600000L);
            String refresh = jwtUtil.createJwt(id, "refresh", role, 86400000L);

            // Refresh 토큰 저장
            refreshTokenService.saveTokens(String.valueOf(id), access, refresh);

            // ----- [중요] 프론트엔드에서 Authorization 헤더 확인 가능하도록 설정
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Authorization", "Bearer " + access);

            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            System.err.println("Successful authentication failed: " + e.getMessage());
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그인 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationException failed) {
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json; charset=utf-8");
            // 프론트에서 error.response.data.message 로 바로 사용할 수 있게 필드명 'message'로 통일
            response.getWriter().write(
                "{\"message\": \"로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.\"}"
            );
        } catch (IOException e) {
            System.err.println("인증 객체 생성 실패 : " + e.getMessage());
            throw new UsersServiceExceptionHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
