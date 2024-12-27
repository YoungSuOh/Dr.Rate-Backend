package com.bitcamp.drrate.domain.users.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter@Setter
    public static class GoogleUserInfo {
        private String email;
        private String name;
        private String sub;
        private String picture;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter@Setter
    public static class ChatRoomUserInfo {
        private String email;
        private String name;
    }

}
