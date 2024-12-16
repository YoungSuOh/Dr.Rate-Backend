package com.bitcamp.drrate.domain.users.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override // 이 메서드를 사용하려면 폼 로그인 기반의 설정을 따로 해주어야함. 지금은 사용하지 않는 메서드
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        //받아오는 
        Users users = usersRepository.findByUserId(userId);
        System.out.println("users = " + users);
        if(users != null) {
            
            return new CustomUserDetails(users);
        }

        return null;
    }
    
}
