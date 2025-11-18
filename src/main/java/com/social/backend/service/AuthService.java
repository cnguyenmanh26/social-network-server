package com.social.backend.service;

import com.social.backend.dto.AuthenticationResponse;
import com.social.backend.dto.LoginRequest;
import com.social.backend.dto.RegisterRequest;
import com.social.backend.dto.ResetPasswordRequest;

public interface AuthService {
    String register(RegisterRequest request);

    // Xác thực OTP để kích hoạt tài khoản
    String verifyOtp(String email, String otp);

    // Đăng nhập (trả về Token)
    AuthenticationResponse login(LoginRequest request);

    // Gửi lại mã OTP (có rate limit)
    String resendOtp(String email);

    // Yêu cầu quên mật khẩu (Gửi OTP)
    String forgotPassword(String email);

    // Đặt lại mật khẩu mới (kèm OTP)
    String resetPassword(ResetPasswordRequest request);
}
