package com.social.backend.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String email; // Hoặc username tùy bạn
    private String password;
}
