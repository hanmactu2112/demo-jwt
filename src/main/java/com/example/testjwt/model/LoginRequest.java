package com.example.testjwt.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    // Constructors, getters, setters
}
