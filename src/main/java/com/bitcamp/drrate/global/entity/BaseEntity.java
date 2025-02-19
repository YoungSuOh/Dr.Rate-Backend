package com.bitcamp.drrate.global.entity;


import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@MappedSuperclass  // JPA에서 이 클래스를 슈퍼 클래스로 지정 -> 이 클래스를 상속받는 하위 엔티티들이 이 클래스의 필드를 공통으로 가지게 됨
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate // 엔티티 처음 생성될 때의 시간
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티 업데이트될 때의 시간
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
