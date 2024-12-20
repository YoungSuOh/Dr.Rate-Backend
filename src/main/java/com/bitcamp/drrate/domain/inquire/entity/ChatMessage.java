package com.bitcamp.drrate.domain.inquire.entity;


import com.bitcamp.drrate.global.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "chat_message")  // MongoDB의 컬렉션 이름
public class ChatMessage extends BaseEntity {
    @Id
    private String id; // MongoDB에서 고유 식별자로 사용될 필드
    private String roomId;    // 채팅방 식별자
    private String senderId;  // 보낸 사람 ID
    private String content;   // 메시지 내용
}