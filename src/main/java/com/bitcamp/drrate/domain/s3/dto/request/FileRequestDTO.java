package com.bitcamp.drrate.domain.s3.dto.request;

import lombok.Getter;
import lombok.Setter;

public class FileRequestDTO {

    @Getter
    @Setter
    public static class FileDeleteRequest {
        private String fileUrl;
    }
}
