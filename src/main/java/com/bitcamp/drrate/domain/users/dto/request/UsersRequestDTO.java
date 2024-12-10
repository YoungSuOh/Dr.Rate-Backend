package com.bitcamp.drrate.domain.users.dto.request;

import com.bitcamp.drrate.domain.users.entity.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UsersRequestDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UsersJoinDTO {
        @NotNull
        private String userId;
        @NotNull
        private String password;
        @NotNull
        private String email;
        @NotNull
        private String nickname;
        @NotNull
        private Role role;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UsersLoginDTO {
        @NotNull
        private String userId;
        @NotNull
        private String password;
    }

}
