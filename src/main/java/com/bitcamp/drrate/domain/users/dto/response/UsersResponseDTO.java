package com.bitcamp.drrate.domain.users.dto.response;

import jakarta.validation.constraints.NotNull;
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
        @NotNull
        private String userId;
        @NotNull
        private String password;
        @NotNull
        private String email;
    }
}
