package com.bitcamp.drrate.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

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
import com.bitcamp.drrate.domain.users.service.CustomUserDetailsService; // 직접 구현된 서비스
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // === 기존 필드들 ===
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UsersRepository usersRepository;
    private final RefreshTokenService refreshTokenService;

    // === (중요) 사용자 정보 조회 서비스를 주입받는다 ===
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * BCrypt 비밀번호 인코더
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager를 Bean으로 등록
     * - 과거 configure(AuthenticationManagerBuilder auth) 대체
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // (1) AuthenticationManagerBuilder를 얻어온 뒤
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);

        // (2) userDetailsService, passwordEncoder 등록
        authBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(bCryptPasswordEncoder());

        // (3) AuthenticationManager 생성
        return authBuilder.build();
    }

    /**
     * Security Filter Chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        // (4) 만든 authenticationManager를 가져온다
        AuthenticationManager authManager = authenticationManager(http);

        // (5) 이 authManager를 Security에 적용
        http.authenticationManager(authManager);

        // === CSRF 비활성화 ===
        http.csrf(csrf -> csrf.disable());

        // === CORS 설정 ===
        // 이미 corsFilter Bean이 있다면, 직접 필터로 등록하거나 http.cors() 등으로 적용 가능
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);

        // === formLogin / httpBasic 비활성화 ===
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // === 접근 권한 설정 ===
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/signIn/**",
                "/api/signUp/**",
                "/ws/**",
                "/api/product/**",
                "/api/products/**",
                "/chat/**",
                "/api/email/**",
                "/api/reissue"
            ).permitAll()
            .requestMatchers(
                "/api/favorite/**",
                "/api/chatmessages/**",
                "/api/s3",
                "/api/calendar",
                "/api/myInfo"
            ).authenticated()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );

        // === 세션 대신 JWT 사용 ===
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // === 커스텀 LoginFilter 등록 ===
        // (6) 여기서 authManager를 이용해 LoginFilter 생성
        http.addFilterAt(
            new LoginFilter(
                authManager,        // 이렇게 교체!
                jwtUtil,
                usersRepository,
                refreshTokenService
            ),
            UsernamePasswordAuthenticationFilter.class
        );

        // === JWT 검사 필터 등록 ===
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

    /**
     * CORS Filter Bean
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
