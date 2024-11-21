package com.bitcamp.drrate.domain.google.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class GoogleUserInfoResponseDTO {
    
    @Getter@Setter
    @NoArgsConstructor
    public static class UserAccessTokenDTO {
        private String accessToken;
        private int expriesIn;
        private String scope;
        private String tokenType;
        private String idToken;
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class UserInfoDTO {
        private String sub;
        private String name;
        private String picture;
        private String email;
    }

}
