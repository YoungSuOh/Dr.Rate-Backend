package com.bitcamp.drrate.domain.inquire.repository;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE m.room.id = :roomId ORDER BY m.createdAt DESC")
    Optional<ChatMessage> findLatestMessageByRoomId(@Param("roomId") Long roomId);

}
