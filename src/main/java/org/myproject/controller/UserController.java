package org.myproject.controller;

import lombok.RequiredArgsConstructor;
import org.myproject.entry.RegisterDTO;
import org.myproject.service.UserService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Long registerUser(@RequestBody RegisterDTO registerDTO) {
        return userService.registerUser(registerDTO);
    }

    @GetMapping("/list")
    public String allUser(){
        return userService.getAllUser();
    }

}
