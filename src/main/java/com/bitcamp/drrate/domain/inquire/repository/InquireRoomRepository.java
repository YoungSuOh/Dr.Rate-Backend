package com.bitcamp.drrate.domain.inquire.repository;

import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InquireRoomRepository extends JpaRepository<InquireRoom, Long> {
    @Query("SELECT r FROM InquireRoom r ORDER BY r.updatedAt DESC")
    List<InquireRoom> findAllOrderByUpdatedAtDesc();
}