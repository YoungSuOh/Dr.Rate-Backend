package com.bitcamp.drrate.domain.inquire.service.chatmessage;


import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import com.mongodb.MongoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public Page<ChatMessage> getMessagesByRoomId(String roomId, int page, int size) {
        try {
            // 페이지 번호와 크기 검증
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Page number or size must be greater than zero.");
            }

            // 페이징 요청
            Pageable pageable = PageRequest.of(page, size);

            // MongoDB에서 메시지 조회
            Page<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
            if (chatMessages.isEmpty()) {
                throw new InquireServiceHandler(ErrorStatus.INQUIRE_MESSAGE_BAD_REQUEST);
            }
            return chatMessages;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new InquireServiceHandler(ErrorStatus.INQUIRE_MESSAGE_BAD_REQUEST);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new InquireServiceHandler(ErrorStatus.INQUIRE_MESSAGE_BAD_REQUEST);
        } catch (MongoException e) {
            e.printStackTrace();
            throw new InquireServiceHandler(ErrorStatus.MONGODB_LOAD_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
