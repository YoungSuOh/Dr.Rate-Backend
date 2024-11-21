package com.bitcamp.drrate.domain.google.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class GoogleUserInfoResponseDTO {
    
    @Builder
    @Getter@Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAccessTokenDTO {
        private String accessToken;
        private int expriesIn;
        private String scope;
        private String tokenType;
        private String idToken;
    }
    
    @Builder
    @Getter@Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {
        private String sub;
        private String name;
        private String picture;
        private String email;
    }

}
