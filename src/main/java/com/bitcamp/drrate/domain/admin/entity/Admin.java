package com.bitcamp.drrate.domain.admin.entity;

import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<InquireRoom> inquireRoom;
}
