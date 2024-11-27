package com.bitcamp.drrate.unit.Inquire;

import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import com.bitcamp.drrate.domain.inquire.repository.InquireRoomRepository;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.domain.users.repository.UsersRepository;
import com.bitcamp.drrate.global.handler.stomp.StompHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StompHandlerTest {

    @Mock
    private InquireRoomRepository inquireRoomRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private StompHandler stompHandler;

    @Test
    void testHandleConnect_validUser() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("userId", "1");

        when(usersRepository.findUsersById(1L)).thenReturn(Optional.of(new Users()));

        // When
        stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null);

        // Then
        verify(usersRepository).findUsersById(1L);
    }

    @Test
    void testHandleSubscribe_validRoom() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/sub/chat/1");

        when(inquireRoomRepository.findById(1L)).thenReturn(Optional.of(new InquireRoom()));

        // When
        stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null);

        // Then
        verify(inquireRoomRepository).findById(1L);
    }

    @Test
    void testHandleSubscribe_invalidRoom() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/sub/chat/999");

        when(inquireRoomRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                stompHandler.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null)
        );
    }
}
