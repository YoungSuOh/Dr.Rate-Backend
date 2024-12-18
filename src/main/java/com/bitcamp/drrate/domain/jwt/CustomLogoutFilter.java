package com.bitcamp.drrate.domain.jwt;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //path and method verify (경로, 요청방식 검증)
        String requestUri = httpRequest.getRequestURI(); //uri에서 path값을 꺼내서 logout요청인지 확인

        //로그아웃이 아니면 다음필터로 넘어감
        if (!requestUri.matches("^\\/logout")) {
            chain.doFilter(request, response);
            return; 
        }

        /* POST 요청인지 확인 아니면 다음 필터로 넘어감*/
        String requestMethod = httpRequest.getMethod();
        if (!requestMethod.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }
        // 요청 헤더에서 Authorization 이라는 값을 받아옴
        String authorizationHeader = httpRequest.getHeader("Authorization");

        String accessToken = null;

        /* 헤더가 있는지 & Access token 올바른 형식인지 */
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); //헤더값이 있고 값의 시작이 Bearer인지 확인 후 있으면 값을 받아옴
        } else { // 없으면 예외처리 후 필터넘김
            throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_HEADER_NOT_FOUND);
        }
        // user pk id값
        Long id = jwtUtil.getId(accessToken);

        /* user pk id 키값으로 refresh token이 있는지 */
        String refreshToken = refreshTokenService.getRefreshToken(String.valueOf(id));

        if (refreshToken == null) { // refresh토큰이 null 일때(1)
            /* (중요) access token이 재발급되고 refresh기간이 만료될 수도 있음 => 이 경우는 access token 유효기간에 따라 로그아웃 권한 부여 */
            if(jwtUtil.isExpired(accessToken)){ // refresh토큰이 null이고 access토큰이 만료되었을때 (1-1)
                // refresh토큰이 없거나 만료되었고 access토큰이 만료되었으면 예외처리
                throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_ACCESS_INVALID); 
            }else{ 
                // refresh토큰이 null이고 access토큰이 만료되지 않았을 때 (1-2)
                String category = jwtUtil.getCategory(refreshToken);  // category == "refresh"
                if(!category.equals("refresh")) { //category가 "refresh"가 아닐 때
                    throw new UsersServiceExceptionHandler(ErrorStatus.SESSION_REFRESH_INVALID);
                }
            }
        }

        refreshTokenService.deleteTokens(String.valueOf(id));
        setAuthorizedResponse(httpResponse); // 성공 메시지 출력
    }
    // 로그아웃 성공시 출력
    private void setAuthorizedResponse(HttpServletResponse response) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.onSuccess(null, SuccessStatus.USER_LOGOUT_SUCCESS);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
