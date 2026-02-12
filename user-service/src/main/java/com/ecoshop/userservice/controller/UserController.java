package com.ecoshop.userservice.controller;

import com.ecoshop.userservice.dto.UserRequest;
import com.ecoshop.userservice.dto.UserResponse;
import com.ecoshop.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        Long newUserId = userService.registerUser(userRequest);
        UserResponse response = new UserResponse("User created successfully", newUserId);

        // Retorna 201 Created com o JSON no corpo
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
