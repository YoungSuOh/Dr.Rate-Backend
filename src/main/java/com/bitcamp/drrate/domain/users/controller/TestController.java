package com.bitcamp.drrate.domain.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.service.UsersService;

import lombok.RequiredArgsConstructor;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class TestController {
    
    private final UsersService usersService;


    @GetMapping("/admin")
    public String adminP() {
        return "Admin Controller";
    }
    @GetMapping("/")
    public String mainP() {
        return "Main Controller";
    }

    @PostMapping("/join")
    public String joinProc(UsersJoinDTO joinDTO) {

        usersService.joinProc(joinDTO);
        return "ok";
    }
}
