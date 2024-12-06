package com.bitcamp.drrate.integration.Inquire;

import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import com.bitcamp.drrate.domain.inquire.entity.InquireRoomStatus;
import com.bitcamp.drrate.domain.inquire.repository.InquireRoomRepository;
import com.bitcamp.drrate.domain.users.entity.Role;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.handler.stomp.StompHandler;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class StompHandlerIntegrationTest {

    @Autowired
    private StompHandler stompHandler;

    @Autowired
    private InquireRoomRepository inquireRoomRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void testHandleSubscribe_validRoom() {
        // Given
        Users user = Users.builder()//.nickName("Test User")
                .userEmail("test@test.com")
                .oauth("kakao")
                .role(Role.USER)
                .build();
        user = usersRepository.save(user); // 영속화
        // 채팅방 생성
        InquireRoom inquireRoom = InquireRoom.builder()
                .users(user)
                .status(InquireRoomStatus.OPEN)
                .build();
        inquireRoomRepository.save(inquireRoom);
        Long roomId = inquireRoom.getId();
        String destination = "/sub/chat/" + roomId;

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);

        // When
        stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null);

        // Then
        Optional<InquireRoom> foundRoom = inquireRoomRepository.findById(roomId);
        assertTrue(foundRoom.isPresent());
    }

    @Test
    void testHandleSubscribe_invalidRoom() {
        // Given
        String destination = "/sub/chat/999";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null)
        );
    }

    @Test
    void testHandleConnect_validUser() {
        // Given
        // 사용자 생성
        Users user = Users.builder()//.nickName("Test User")
                .userEmail("test@test.com")
                .oauth("kakao")
                .role(Role.USER)
                .build();
        usersRepository.save(user);
        Long userId = user.getId();

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("userId", String.valueOf(userId));

        // When
        stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null);

        // Then
        Optional<Users> foundUser = usersRepository.findById(userId);
        assertTrue(foundUser.isPresent());
    }

    @Test
    void testHandleConnect_invalidUser() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("userId", "999");

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null)
        );
    }
}

