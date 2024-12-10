package com.bitcamp.drrate.domain.users.entity;

import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import com.bitcamp.drrate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // 엔티티 테이블 이름 지정 필수 !!
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, name = "user_id")
    private String userId;

    @Column(name = "user_pwd", nullable = true)
    private String password;

    @Column(nullable = false, name = "user_email", unique = true)
    private String email;

    @Column(nullable = false, name = "user_name")
    private String nickName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "oauth_provider", nullable = false)
    private String oauth;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "users", fetch = FetchType.LAZY, optional = true)
    private InquireRoom inquireRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
