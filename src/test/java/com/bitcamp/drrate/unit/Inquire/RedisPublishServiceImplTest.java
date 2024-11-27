package com.bitcamp.drrate.unit.Inquire;

import com.bitcamp.drrate.domain.inquire.dto.response.ChatMessageDTO;
import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.bitcamp.drrate.domain.inquire.repository.InquireRoomRepository;
import com.bitcamp.drrate.domain.inquire.service.publish.RedisPublishServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RedisPublishServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private InquireRoomRepository inquireRoomRepository;

    @InjectMocks
    private RedisPublishServiceImpl redisPublishService;

    @Test
    void testPublish_validMessage() {
        // Given
        ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                .roomId("1")
                .message("Hello, Redis!")
                .build();

        InquireRoom inquireRoom = InquireRoom.builder().build();

        ChatMessage chatMessage = ChatMessage.builder()
                .room(inquireRoom)
                .message("Hello, Redis!")
                .build();

        ChannelTopic topic = new ChannelTopic("/sub/chat/" + chatMessageDTO.getRoomId());

        when(inquireRoomRepository.findById(1L)).thenReturn(Optional.of(inquireRoom));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // When
        redisPublishService.publish(topic, chatMessageDTO);

        // Then
        verify(redisTemplate).convertAndSend(topic.getTopic(), chatMessageDTO);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }
}
