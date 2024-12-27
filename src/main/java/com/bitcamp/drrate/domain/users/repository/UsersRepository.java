package com.bitcamp.drrate.domain.users.repository;

import com.bitcamp.drrate.domain.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findUsersById(Long id);

    Page<Users> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 이름으로 검색
    Page<Users> findByUsernameContainingIgnoreCaseOrderByCreatedAtDesc(String username, Pageable pageable);

    // 이메일로 검색
    Page<Users> findByEmailContainingIgnoreCaseOrderByCreatedAtDesc(String email, Pageable pageable);

    Optional<Users> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByUserId(String userId);

    Users findByUserId(String userId);

    boolean existsBySocial(String social);

}
