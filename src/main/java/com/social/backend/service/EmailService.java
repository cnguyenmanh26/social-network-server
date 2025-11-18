package com.social.backend.service;

public interface EmailService {
    void sendOtp(String toEmail, String otp);
}