package com.ecoshop.userservice.service;

import com.ecoshop.userservice.dto.AuthResponse;
import com.ecoshop.userservice.dto.LoginRequest;
import com.ecoshop.userservice.dto.UserRequest;

public interface UserService {

    Long registerUser(UserRequest userRequest);
    AuthResponse login(LoginRequest loginRequest);
}
