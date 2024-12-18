package com.bitcamp.drrate.domain.inquire.repository;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.entity.ChatRoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("MongoChatRoomRepository")
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Page<ChatRoom> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    // 방 번호(roomId) 검색
    Page<ChatRoom> findByIdContainingIgnoreCaseOrderByUpdatedAt(String roomId, Pageable pageable);

    // 이메일 검색
    Page<ChatRoom> findByEmailContainingIgnoreCaseOrderByUpdatedAt(String email, Pageable pageable);

    // 이름 검색
    Page<ChatRoom> findByUserNameContainingIgnoreCaseOrderByUpdatedAt(String name, Pageable pageable);


    long countByStatus(ChatRoomStatus status);
}