package com.bitcamp.drrate.domain.inquire.repository;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoomImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("MongoChatRoomImageRepository")
public interface ChatRoomImageRepository extends MongoRepository<ChatRoomImage, String> {
    @Query("{ 'id': ?0 }")
    void deleteAllById(String id);
}
