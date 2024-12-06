package com.bitcamp.drrate.domain.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitcamp.drrate.domain.users.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users>findUsersById(Long id);

    Optional<Users>findByUserEmail(String userEmail);
}
