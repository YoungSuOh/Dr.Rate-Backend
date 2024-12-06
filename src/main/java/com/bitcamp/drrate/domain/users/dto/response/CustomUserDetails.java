package com.bitcamp.drrate.domain.users.dto.response;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bitcamp.drrate.domain.users.entity.Users;


public class CustomUserDetails implements UserDetails {

    @Autowired
    private Users users;

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return users.getRole().toString();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return users.getUserPwd();
    }

    @Override
    public String getUsername() {
        return users.getUserName();
    }
    
}
