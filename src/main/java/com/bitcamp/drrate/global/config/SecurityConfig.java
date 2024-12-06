package com.bitcamp.drrate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.bitcamp.drrate.domain.users.jwt.CustomLogoutFilter;
import com.bitcamp.drrate.domain.users.jwt.JWTFilter;
import com.bitcamp.drrate.domain.users.jwt.JWTUtil;
import com.bitcamp.drrate.domain.users.jwt.LoginFilter;
import com.bitcamp.drrate.domain.users.repository.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository; //리프레시 토큰
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(auth -> auth.disable());
        //Form 로그인 방식 비활성화
        http.formLogin(auth -> auth.disable());
        //http basic 인증 방식 비활성화]
        http.httpBasic(auth -> auth.disable());

        //경로에 대한 권한 설정
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/user/loginForm", "/", "/user/joinForm", "/login", "/customLogin").permitAll()
            .requestMatchers("/admin").hasRole("ADMIN")
            .requestMatchers("/reissue").permitAll()
            .anyRequest().authenticated());
        //커스텀 필터 설정 (폼로그인 대신 설정)
        http
            .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http
            .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
            //내가 만든 LoginFilter.java를 생성해서 등록, addFilterAt은 사용할 필터의 위치를 지정, UsernamePasswordAuthenticationFilter 필터를 대체하는 필터를 설정해주는것 
        
        http
            .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // http
        //     .formLogin(login -> login.defaultSuccessUrl("/customLogin", true));

        //세션 설정 (JWT토큰 방식에서는 세션을 StateLess )방식으로 관리하기 때문에 세션 설정을 해주어야함)
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
