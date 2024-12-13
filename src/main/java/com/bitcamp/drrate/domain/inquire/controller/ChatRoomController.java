package com.bitcamp.drrate.domain.inquire.controller;


import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.chatroom.ChatRoomService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/inquireList")
    public ApiResponse<Page<ChatRoom>> getChatRoomsSortedByUpdatedAt(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        try{
            Page<ChatRoom> result = chatRoomService.getChatRoomsSortedByUpdatedAt(page, size);
            return ApiResponse.onSuccess(result, SuccessStatus.INQUIRE_LIST_GET_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_LIST_GET_FAILED.getCode(), ErrorStatus.INQUIRE_LIST_GET_FAILED.getMessage(), null);
        }
    }
}