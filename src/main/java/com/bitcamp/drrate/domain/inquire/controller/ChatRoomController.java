package com.bitcamp.drrate.domain.inquire.controller;


import com.bitcamp.drrate.domain.inquire.entity.ChatRoom;
import com.bitcamp.drrate.domain.inquire.service.chatroom.ChatRoomService;
import com.bitcamp.drrate.domain.inquire.service.kafka.KafkaTopicService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final KafkaTopicService kafkaTopicService;

    @GetMapping("/admin/chatrooms/inquireList")
    public ApiResponse<Page<ChatRoom>> getChatRoomsBySearchCriteria(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "searchType",required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        try {
            Page<ChatRoom> result = chatRoomService.getChatRoomsBySearchCriteria(page, size, searchType, keyword);
            return ApiResponse.onSuccess(result, SuccessStatus.INQUIRE_LIST_GET_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_LIST_GET_FAILED.getCode(), ErrorStatus.INQUIRE_LIST_GET_FAILED.getMessage(), null);
        }
    }

    @GetMapping("/topic/check/{topicName}")
    public ApiResponse<HttpStatus> checkTopicExists(@PathVariable("topicName") String topicName) {
        try{
            System.out.println(topicName);
            if(kafkaTopicService.topicExists(topicName)){
                return ApiResponse.onSuccess(HttpStatus.OK ,SuccessStatus.KAFKA_TOPIC_GET_SUCCESS);
            }else{
                return ApiResponse.onFailure(ErrorStatus.KAFKA_TOPIC_NOT_FOUND.getCode(), ErrorStatus.KAFKA_TOPIC_NOT_FOUND.getMessage(), null);
            }
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.KAFKA_BROKER_BADREQUEST.getCode(), ErrorStatus.KAFKA_BROKER_BADREQUEST.getMessage(), null);
        }
    }

    @PostMapping("/chatrooms/create")
    public ApiResponse<HttpStatus> createChatRoom(@RequestBody Map<String, String> payload) {
        try {
            String id = payload.get("id");
            System.out.println("id: " + id);
            chatRoomService.getOrCreateChatRoom(id);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.INQUIRE_ROOM_CREATE_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_CREATED_FAILED.getCode(), ErrorStatus.INQUIRE_CREATED_FAILED.getMessage(), null);
        }
    }

    @DeleteMapping("/chatrooms/{id}")
    public ApiResponse<HttpStatus> deleteChatRoom(@PathVariable(value = "id") String id) {
        try{
            chatRoomService.deleteChatRoomById(id);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.INQUIRE_ROOM_DELETE_SUCCESS);
        }catch (Exception e){
            return ApiResponse.onFailure(ErrorStatus.INQUIRE_DELETE_FAILED.getCode(), ErrorStatus.INQUIRE_DELETE_FAILED.getMessage(), null);
        }
    }
}