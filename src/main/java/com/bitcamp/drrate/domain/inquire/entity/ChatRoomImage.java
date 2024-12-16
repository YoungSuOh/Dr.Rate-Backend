package com.bitcamp.drrate.domain.inquire.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "chatroom_image")
public class ChatRoomImage {
    @Id
    private String id;
    private String imageUrl;
}
