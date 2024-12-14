package com.zerobase.naverbox.controller;

import com.zerobase.naverbox.service.JwtService;
import com.zerobase.naverbox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/success")
    public ResponseEntity success(@RequestHeader("Authorization") String token){
        String userId = jwtService.extractUserId(token);
        return new ResponseEntity("성공", HttpStatus.OK);
    }
}
