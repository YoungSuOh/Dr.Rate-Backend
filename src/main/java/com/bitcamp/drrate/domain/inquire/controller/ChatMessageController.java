package com.bitcamp.drrate.domain.inquire.controller;

import com.bitcamp.drrate.domain.inquire.entity.ChatMessage;
import com.bitcamp.drrate.domain.inquire.service.chatmessage.ChatMessageService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatmessages")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/list")
    public ApiResponse<Page<ChatMessage>> getChatMessagesByRoomId(
            @RequestParam String roomId,
            @RequestParam(defaultValue = "0") int page, // 기본 페이지 0
            @RequestParam(defaultValue = "15") int size // 기본 크기 15
    ) {
        try{
            Page<ChatMessage>result = chatMessageService.getMessagesByRoomId(roomId, page, size);
            return ApiResponse.onSuccess(result, SuccessStatus.INQUIRE_MESSAGE_GET_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_MESSAGE_GET_FAILED.getCode(), ErrorStatus.INQUIRE_MESSAGE_GET_FAILED.getMessage(),null);
        }
    }
}