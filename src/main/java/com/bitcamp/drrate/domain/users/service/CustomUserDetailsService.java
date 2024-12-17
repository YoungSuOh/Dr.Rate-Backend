package com.bitcamp.drrate.domain.users.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // usersRepository에서 userId로 사용자 찾기
        Users users = usersRepository.findByUserId(userId);
        
        if (users == null) {
            throw new UsersServiceExceptionHandler(ErrorStatus.USER_ID_CANNOT_FOUND);
        }

        // 사용자 정보를 UserDetails로 변환하여 반환
        return new CustomUserDetails(users);
    }
    
}
