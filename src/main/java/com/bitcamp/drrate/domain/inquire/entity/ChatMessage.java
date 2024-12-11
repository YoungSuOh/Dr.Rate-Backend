package com.bitcamp.drrate.domain.inquire.entity;


import com.bitcamp.drrate.global.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "chatMessages")  // MongoDB의 컬렉션 이름
public class ChatMessage extends BaseEntity {
    private String roomId;
    private String sender;
    private String content;
}