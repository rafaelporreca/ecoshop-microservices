package com.ecoshop.userservice.dto;

public record UserRequest (
    String email,
    String password,
    String fullName
){}
