package com.bitcamp.drrate.domain.inquire.repository;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoomImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MongoChatRoomImageRepository")
public interface ChatRoomImageRepository extends MongoRepository<ChatRoomImage, String> {
    @Query(value = "{ 'roomId': :#{#roomId} }")
    List<ChatRoomImage> findAllByRoomId(@Param("roomId") String roomId);


    @Query(value = "{ 'roomId': ?0 }", delete = true)
    void deleteAllByRoomId(String roomId);
}
