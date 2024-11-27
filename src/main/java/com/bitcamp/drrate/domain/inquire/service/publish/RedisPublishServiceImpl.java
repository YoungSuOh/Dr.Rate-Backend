package com.bitcamp.drrate.domain.inquire.service.publish;

import com.bitcamp.drrate.domain.inquire.dto.response.ChatMessageDTO;
import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.entity.InquireRoom;
import com.bitcamp.drrate.domain.inquire.entity.InquireRoomStatus;
import com.bitcamp.drrate.domain.inquire.repository.ChatMessageRepository;
import com.bitcamp.drrate.domain.inquire.repository.InquireRoomRepository;
import com.bitcamp.drrate.domain.users.entity.Users;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisPublishServiceImpl implements RedisPublishService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, ChannelTopic> channelCache;
    private final InquireRoomRepository inquireRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    @Override
    public void publish(ChannelTopic topic, ChatMessageDTO chatMessageDTO/*, Users sender*/) {
        if (topic == null || chatMessageDTO == null) {
            throw new IllegalArgumentException(ErrorStatus.INQUIRE_INVALID_ARGUMENT.getMessage());
        }
        redisTemplate.convertAndSend(topic.getTopic(), chatMessageDTO);

        // Optional에서 InquireRoom 추출
        InquireRoom inquireRoom = inquireRoomRepository.findById(Long.parseLong(chatMessageDTO.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException(ErrorStatus.INQUIRE_ROOM_NOT_FOUND.getMessage()));

        chatMessageRepository.save(ChatMessage.builder()
                /*.sender(sender)*/
                .room(inquireRoom)
                .message(chatMessageDTO.getMessage())
                .build());
    }


    public ChannelTopic getOrCreateChannel(String roomId/*, Users user*/) {
        Long id = Long.parseLong(roomId);
        // 채팅방 확인 및 생성 또는 업데이트
        inquireRoomRepository.findById(id).ifPresentOrElse(
                room -> {
                    // 문의 내역이 올라왔으므로 updatedAt 갱신
                    System.out.println("old");
                    room.setUpdatedAt(LocalDateTime.now());
                    inquireRoomRepository.save(room);
                },
                () -> {
                    System.out.println("new");
                    Users users = Users.builder().id(id).build();
                    // 채팅방이 없으면 새로 생성
                    InquireRoom newRoom = InquireRoom.builder()
                            .users(users)
                            .status(InquireRoomStatus.OPEN)
                            .build();
                    inquireRoomRepository.save(newRoom);
                }
        );

        // Redis 채널 캐싱 및 반환
        return channelCache.computeIfAbsent(roomId, key -> new ChannelTopic("/sub/chat/" + key));
    }
}
