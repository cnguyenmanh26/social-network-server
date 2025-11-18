package com.social.backend.service.impl;

import com.social.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã OTP xác thực đăng ký");
        message.setText("Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 3 phút.");
        mailSender.send(message);
        System.out.println("Mail sent to " + toEmail); // Log ra console để debug
    }
}
