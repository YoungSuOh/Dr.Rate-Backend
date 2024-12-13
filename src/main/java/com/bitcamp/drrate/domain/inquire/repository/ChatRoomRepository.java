package com.bitcamp.drrate.domain.inquire.repository;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("MongoChatRoomRepository")
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Page<ChatRoom> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}