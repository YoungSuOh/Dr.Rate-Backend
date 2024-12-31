package com.bitcamp.drrate.domain.users.entity;



import com.bitcamp.drrate.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = true, name = "social")
    private String social;

}
