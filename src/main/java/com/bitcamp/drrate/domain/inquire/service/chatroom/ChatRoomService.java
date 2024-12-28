package com.bitcamp.drrate.domain.inquire.service.chatroom;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import org.springframework.data.domain.Page;

public interface ChatRoomService {
    ChatRoom getOrCreateChatRoom(String senderId);
    Page<ChatRoom> getChatRoomsBySearchCriteria(int page, int size, String searchType, String keyword);
    void deleteChatRoomById(String id);
    void createChatRoom(String senderId);
}
