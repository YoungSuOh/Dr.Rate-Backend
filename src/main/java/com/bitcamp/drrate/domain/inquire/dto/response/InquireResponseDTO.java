package com.bitcamp.drrate.domain.inquire.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquireResponseDTO implements Serializable {
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime response_at;
}
