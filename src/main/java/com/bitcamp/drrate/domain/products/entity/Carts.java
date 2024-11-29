package com.bitcamp.drrate.domain.products.entity;

import com.bitcamp.drrate.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carts", uniqueConstraints = @UniqueConstraint(columnNames = {"ct_user_id", "ct_prd_id"})) // 엔티티 테이블 이름 지정 필수 !!
@Data
public class Carts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ct_user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "ct_prd_id")
    private Products products;
}
