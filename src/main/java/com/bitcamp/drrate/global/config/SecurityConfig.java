package com.bitcamp.drrate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.bitcamp.drrate.domain.jwt.CustomLogoutFilter;
import com.bitcamp.drrate.domain.jwt.JWTFilter;
import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.jwt.LoginFilter;
import com.bitcamp.drrate.domain.jwt.refresh.RefreshTokenService;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.domain.users.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // === 의존성 ===
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UsersRepository usersRepository;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * BCrypt 비밀번호 인코더
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager를 HttpSecurity 기반으로 생성
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // (1) AuthenticationManagerBuilder를 꺼낸다
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // (2) userDetailsService + passwordEncoder 등록
        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());

        // (3) 빌드
        return authBuilder.build();
    }

    /**
     * CORS 설정 Bean
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 쿠키 포함 허용
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedOriginPattern("https://www.dr-rate.store");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // 필요하다면 exposeHeaders, e.g. config.addExposedHeader("Authorization");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * Security Filter Chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        // (4) authManager 생성
        AuthenticationManager authManager = authenticationManager(http);

        // (5) http에 authManager 적용
        http.authenticationManager(authManager);

        // === CSRF 비활성화 ===
        http.csrf(csrf -> csrf.disable());

        // === CORS 필터 등록 (UsernamePasswordAuthenticationFilter 전에) ===
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);

        // === formLogin / httpBasic 비활성화 ===
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // === 경로별 접근 권한 ===
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/healthCheck",
                        "/api/signIn/**",
                        "/api/signUp/**",
                        "/ws/**",
                        "/api/product/getOneProduct/**",
                        "/api/product/getAllProducts",
                        "/api/product/guest/**",
                        "/chat/**",
                        "/api/email/**",
                        "/api/reissue",
                        "/api/trackVisit"
                ).permitAll()
                .requestMatchers(
                        "/api/favorite/**",
                        "/api/chatmessages/**",
                        "/api/s3",
                        "/api/calendar",
                        "/api/myInfo",
                        "/api/logout",
                        "/api/myInfoEdit",
                        "/api/product/getProduct",
                        "/api/deleteAccount",
                        "/api/emailinquire/**",
                        "/api/inquiries"
                ).authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        // === 세션 대신 JWT 사용 ===
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // === 커스텀 LoginFilter 등록 ===
        // 기존에 "http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), ...))"
        // 이런 식으로 authenticationConfiguration을 직접 넘기던 부분은 제거
        // 한 번만 등록하면 됨
        http.addFilterAt(
                new LoginFilter(authManager, jwtUtil, usersRepository, refreshTokenService),
                UsernamePasswordAuthenticationFilter.class
        );

        // === JWT 검사 필터 등록 ===
        // LoginFilter 이후에 동작하도록, UsernamePasswordAuthenticationFilter 앞뒤 조정 가능
        // 여기서는 "UsernamePasswordAuthenticationFilter.class" 앞에 등록
        http.addFilterBefore(
                new JWTFilter(jwtUtil, usersRepository),
                UsernamePasswordAuthenticationFilter.class
        );

        // === 커스텀 로그아웃 필터 등록 ===
        http.addFilterBefore(
                new CustomLogoutFilter(jwtUtil, objectMapper, refreshTokenService),
                LogoutFilter.class
        );

        return http.build();
    }
}
