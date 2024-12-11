package com.bitcamp.drrate.domain.inquire.entity;


import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Table(name = "inquire_room")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class InquireRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquireRoomStatus status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>(); // 문의방의 메시지 목록
}
