package com.bitcamp.drrate.domain.inquire.entity;


import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private InquireRoom room; // InquireRoom과의 관계 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
}