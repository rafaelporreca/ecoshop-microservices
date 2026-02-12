package com.ecoshop.userservice.service;

import com.ecoshop.userservice.dto.UserRequest;

public interface UserService {

    Long registerUser(UserRequest userRequest);
}
