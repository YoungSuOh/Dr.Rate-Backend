package com.bitcamp.drrate.domain.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UsersRepository usersRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("Authorization");

        // 토큰이 없으면 다음 필터로 넘김
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 토큰 추출
        accessToken = accessToken.substring(7); // Remove "Bearer " prefix

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        // try{
        //     jwtUtil.isExpired(accessToken);
        // } catch(ExpiredJwtException e) {
        //     //response body
        //     PrintWriter writer = response.getWriter();
        //     writer.print("access token expired");
        //     //response status code
        //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //     return;
        // } // try/catch
        // // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        // String category = jwtUtil.getCategory(accessToken);

        // if(!category.equals("access")) {
        //     //response body
        //     PrintWriter writer = response.getWriter();
        //     writer.print("invalid access token");

        //     //response status code
        //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //     return;
        // } // if

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try{
            jwtUtil.isExpired(accessToken);
        } catch(ExpiredJwtException e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_ACCESS_EXPIRED);
        } // try/catch

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        try {
            String category = jwtUtil.getCategory(accessToken);
            if(!category.equals("access")) {
                throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_ACCESS_INVALID);
            }
        } catch(UsersServiceExceptionHandler e) {
            throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_ACCESS_INVALID);
        }

        Long id = jwtUtil.getId(accessToken);

        System.out.println("user_pk_id: " + id);

        Users users = usersRepository.findUsersById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + id));

        
        CustomUserDetails customUserDetails = new CustomUserDetails(users);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
