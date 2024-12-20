package com.bitcamp.drrate.domain.inquire.entity;


import com.bitcamp.drrate.global.entity.BaseEntity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@Document(collection = "chatroom")
public class ChatRoom extends BaseEntity {
    @Id
    private String id;
    private String topicName;
    private String userName;
    private String email;
    private ChatRoomStatus status;
}
