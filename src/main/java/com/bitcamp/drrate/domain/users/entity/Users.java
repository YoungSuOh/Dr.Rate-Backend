package com.bitcamp.drrate.domain.users.entity;


import com.bitcamp.drrate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
