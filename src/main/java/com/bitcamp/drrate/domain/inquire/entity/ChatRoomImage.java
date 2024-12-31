package com.bitcamp.drrate.domain.inquire.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@Document(collection = "chatroom_image")
public class ChatRoomImage {
    @Id
    private String id;
    @Field("roomId")
    private String roomId;
    @Field("imageUrl")
    private String imageUrl;
}
