package com.bitcamp.drrate.domain.users.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.drrate.domain.users.dto.request.UsersRequestDTO.UsersJoinDTO;
import com.bitcamp.drrate.domain.users.service.UsersService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final UsersService usersService;


    @GetMapping("/admin")
    @ResponseBody
    public String adminP() {
        return "Admin Controller";
    }
    @GetMapping("/")
    @ResponseBody
    public String mainP() {
        return "Main Controller";
    }

    @PostMapping("/join")
    @ResponseBody
    public String joinProc(UsersJoinDTO joinDTO) {
<<<<<<< Updated upstream
=======
        System.out.println(joinDTO.getEmail());
        System.out.println(joinDTO.getPassword());
        System.out.println(joinDTO.getUserId());
        System.out.println(joinDTO.getUsername());
>>>>>>> Stashed changes
        usersService.joinProc(joinDTO);
        return "ok";
    }

    @GetMapping("/loginForm")
    public String loginP() {
        return "loginForm";
    }
}
