package com.social.backend.controller;

import com.social.backend.dto.*;
import com.social.backend.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authServiceImpl.login(request))
                .build();
    }
    @PostMapping("/register")
    // Trả về ApiResponse<String>
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        // Không cần try-catch nữa!
        return ApiResponse.<String>builder()
                .result(authServiceImpl.register(request))
                .message("Đăng ký thành công, vui lòng check mail nhap otp")
                .build();
    }

    @PostMapping("/verify")
    public ApiResponse<String> verify(@RequestBody VerifyOtpRequest request) {
        return ApiResponse.<String>builder()
                .result(authServiceImpl.verifyOtp(request.getEmail(), request.getOtp()))
                .build();
    }

    @PostMapping("/resend")
    public ApiResponse<String> resend(@RequestBody ResendOtpRequest request) {
        return ApiResponse.<String>builder()
                .result(authServiceImpl.resendOtp(request.getEmail()))
                .build();
    }
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody ResendOtpRequest request) {
        // Tái sử dụng ResendOtpRequest vì nó chỉ có mỗi field email
        return ApiResponse.<String>builder()
                .result(authServiceImpl.forgotPassword(request.getEmail()))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return ApiResponse.<String>builder()
                .result(authServiceImpl.resetPassword(request))
                .build();
    }
}