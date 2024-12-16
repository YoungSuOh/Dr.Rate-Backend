package com.bitcamp.drrate.domain.inquire.controller;


import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.chatroom.ChatRoomService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/inquireList")
    public ApiResponse<Page<ChatRoom>> getChatRoomsBySearchCriteria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword
    ) {
        try {
            Page<ChatRoom> result = chatRoomService.getChatRoomsBySearchCriteria(page, size, searchType, keyword);
            return ApiResponse.onSuccess(result, SuccessStatus.INQUIRE_LIST_GET_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_LIST_GET_FAILED.getCode(), ErrorStatus.INQUIRE_LIST_GET_FAILED.getMessage(), null);
        }
    }


    @DeleteMapping("/{id}")
    public ApiResponse<HttpStatus> deleteChatRoom(@PathVariable String id) {
        try{
            chatRoomService.deleteChatRoomById(id);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.INQUIRE_ROOM_DELETE_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_DELETE_FAILED.getCode(), ErrorStatus.INQUIRE_DELETE_FAILED.getMessage(), null);
        }
    }
}