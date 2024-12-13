package com.bitcamp.drrate.domain.inquire.service.chatmessage;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import org.springframework.data.domain.Page;

public interface ChatMessageService {
    public Page<ChatMessage> getMessagesByRoomId(String roomId, int page, int size);
}
