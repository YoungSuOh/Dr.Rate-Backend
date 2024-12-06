package com.bitcamp.drrate.domain.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UsersResponseDTO {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UsersMyPageDto {
        private String userId;
        private String userPwd;
        private String userName;
        private String userEmail;
        private String role;
        //private String birth;
    }
}
