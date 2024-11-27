package com.bitcamp.drrate.domain.inquire.dto.response;

import com.bitcamp.drrate.domain.users.entity.Users;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO implements Serializable {
    private String roomId;
    private String message;
}
