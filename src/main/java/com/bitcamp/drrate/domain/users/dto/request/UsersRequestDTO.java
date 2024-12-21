package com.bitcamp.drrate.domain.users.dto.request;

import com.bitcamp.drrate.domain.users.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UsersRequestDTO {

    @Builder
    @Getter@Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsersJoinDTO {
        @NotNull
        private String userId;
        @NotNull
        private String password;
        @NotNull
        private String email;
        @NotNull
        private String username;

        private String birthdate;
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
