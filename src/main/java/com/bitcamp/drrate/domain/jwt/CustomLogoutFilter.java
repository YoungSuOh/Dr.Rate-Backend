package com.bitcamp.drrate.domain.jwt;

import java.io.IOException;

import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //path and method verify
        String requestUri = request.getRequestURI(); //uri에서 path값을 꺼내서 logout요청인지 확인
        if (!requestUri.matches("^\\/logout")) {
            filterChain.doFilter(request, response);
            return; //로그아웃이 아니면 다음필터로 넘어감
        }
        /* POST 요청인지 */
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;

        /* 헤더가 있는지 & Access token 올바른 형식인지 */
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        } else {
            setUnauthorizedResponse(response, ErrorStatus.SESSION_HEADER_NOT_FOUND);
            return;
        }

        String userId = jwtUtil.getUserId(accessToken);

        /* userId 키값으로 refresh token이 있는지 */
        String refreshToken = refreshTokenService.getRefreshToken(userId);
        if (refreshToken == null) {
            /* (중요) access token이 재발급되고 refresh기간이 만료될 수도 있음 => 이 경우는 access token 유효기간에 따라 로그아웃 권한 부여 */
            if(jwtUtil.isExpired(accessToken)){
                setUnauthorizedResponse(response, ErrorStatus.SESSION_ACCESS_NOT_VALID); return;
            }else{
                String category = jwtUtil.getCategory(refreshToken);
                if(!category.equals("refresh")) {
                    setUnauthorizedResponse(response,ErrorStatus.SESSION_REFRESH_NOT_VALID);
                    return;
                }
            }
        }
        refreshTokenService.deleteTokens(userId);
        setAuthorizedResponse(response);
    }
    private void setAuthorizedResponse(HttpServletResponse response) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.onSuccess(null, SuccessStatus.USER_LOGOUT_SUCCESS);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private void setUnauthorizedResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                errorStatus.getCode(),
                errorStatus.getMessage(),
                null
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
