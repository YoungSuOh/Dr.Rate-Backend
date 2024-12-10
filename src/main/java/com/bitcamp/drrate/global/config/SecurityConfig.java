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

import com.bitcamp.drrate.domain.jwt.CustomLogoutFilter;
import com.bitcamp.drrate.domain.jwt.JWTFilter;
import com.bitcamp.drrate.domain.jwt.JWTUtil;
import com.bitcamp.drrate.domain.jwt.LoginFilter;
import com.bitcamp.drrate.domain.users.repository.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        UsernamePasswordAuthenticationFilter authFilter = new UsernamePasswordAuthenticationFilter();
        authFilter.setUsernameParameter("userId"); //스프링 시큐리티에서 사용자 인증을 위해 필요로 하는 인자값은 username과 password임
        //그런데 보통 id값과 password값을 많이 사용하는데 이걸 변경해주는 코드가 UsernamePasswordAuthenticationFilter내부에있음.
        authFilter.setPasswordParameter("password");

        //csrf disable
        http.csrf(auth -> auth.disable());
        //폼 로그인 방식 disalbe
        http.formLogin(auth -> auth.disable());
        //http basic 인증 방식 disable
        http.httpBasic(auth -> auth.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/", "/join", "/reissue", "/login/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
        //세션 설정
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
