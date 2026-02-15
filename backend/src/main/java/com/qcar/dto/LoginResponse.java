package com.qcar.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String username;
}