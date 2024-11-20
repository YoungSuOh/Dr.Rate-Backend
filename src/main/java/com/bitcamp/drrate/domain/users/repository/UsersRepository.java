package com.bitcamp.drrate.domain.users.repository;

import com.bitcamp.drrate.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users>findUsersById(Long id);

    Optional<Users>findByEmail(String email);
}
