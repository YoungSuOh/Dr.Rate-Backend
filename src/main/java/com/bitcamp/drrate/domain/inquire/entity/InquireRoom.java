package com.bitcamp.drrate.domain.inquire.entity;

import com.bitcamp.drrate.domain.admin.entity.Admin;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.global.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "InquireRoom")
public class InquireRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Users users;

    @ManyToOne
    private Admin admin;
}
