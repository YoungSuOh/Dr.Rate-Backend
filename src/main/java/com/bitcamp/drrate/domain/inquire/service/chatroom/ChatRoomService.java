package com.bitcamp.drrate.domain.inquire.service.chatroom;

import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import org.springframework.data.domain.Page;

public interface ChatRoomService {
    public ChatRoom getOrCreateChatRoom(String senderId);
    Page<ChatRoom> getChatRoomsSortedByUpdatedAt(int page, int size);
}
