package com.social.backend.service.impl;

import com.social.backend.dto.AuthenticationResponse;
import com.social.backend.dto.LoginRequest;
import com.social.backend.dto.RegisterRequest;
import com.social.backend.dto.ResetPasswordRequest;
import com.social.backend.entity.User;
import com.social.backend.exception.AppException;
import com.social.backend.exception.ErrorCode;
import com.social.backend.repository.UserRepository;

import com.social.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailServiceImpl;
    private final StringRedisTemplate redisTemplate;
    private static final String RESEND_BLOCK_PREFIX = "resend_block:";
    private final JwtService jwtService;
    // Logic 1: Đăng ký
    @Transactional
    public String register(RegisterRequest request) {
        // Check trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        // Lưu User (PENDING)
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .status("PENDING") // Chưa kích hoạt
                .build();
        userRepository.save(user);

        // Sinh OTP và Lưu Redis (3 phút)
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        redisTemplate.opsForValue().set("otp:" + request.getEmail(), otp, Duration.ofMinutes(3));

        // Gửi Mail
        emailServiceImpl.sendOtp(request.getEmail(), otp);

        return "Đăng ký thành công. Hãy kiểm tra email để lấy OTP!";
    }

    // Logic 2: Xác thực OTP
    public String verifyOtp(String email, String otpInput) {
        String redisKey = "otp:" + email;
        String cachedOtp = redisTemplate.opsForValue().get(redisKey);

        if (cachedOtp == null) {
            throw new RuntimeException("OTP đã hết hạn hoặc không tồn tại.");
        }

        if (!cachedOtp.equals(otpInput)) {
            throw new RuntimeException("OTP không chính xác.");
        }

        // OTP đúng -> Active User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setStatus("ACTIVE");
        userRepository.save(user);

        // Xóa OTP khỏi Redis
        redisTemplate.delete(redisKey);

        return "Tài khoản đã được kích hoạt thành công!";
    }
    public String resendOtp(String email) {
        // 1. Kiểm tra User có tồn tại không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa được đăng ký!"));

        // 2. Nếu User đã Active rồi thì không gửi nữa
        if ("ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("Tài khoản này đã được kích hoạt rồi. Vui lòng đăng nhập.");
        }

        // 3. KIỂM TRA SPAM (Rate Limiting)
        // Kiểm tra xem key chặn có tồn tại không
        String blockKey = RESEND_BLOCK_PREFIX + email;
        if (redisTemplate.hasKey(blockKey)) {
            throw new AppException(ErrorCode.SPAM_OTP);
        }

        // 4. Sinh mã OTP mới
        String newOtp = String.valueOf(100000 + new Random().nextInt(900000));

        // 5. Lưu OTP mới vào Redis (Ghi đè mã cũ, gia hạn lại 3 phút)
        redisTemplate.opsForValue().set(
                "otp:" + email,
                newOtp,
                Duration.ofMinutes(3)
        );

        // 6. Đặt cờ chặn Spam trong 60 giây
        redisTemplate.opsForValue().set(
                blockKey,
                "BLOCKED",
                Duration.ofSeconds(60)
        );

        // 7. Gửi mail
        emailServiceImpl.sendOtp(email, newOtp);

        return "Mã OTP mới đã được gửi. Vui lòng kiểm tra email.";
    }

    public AuthenticationResponse login(LoginRequest request) {
        // 1. Tìm User theo Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Kiểm tra Password (So sánh Hash)
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED); // Lỗi sai password
        }

        // 3. Kiểm tra Status (Phải Active mới cho vào)
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED); // Hoặc lỗi riêng: USER_NOT_ACTIVE
        }

        // 4. Tạo Token
        String token = jwtService.generateToken(user.getUsername());

        // 5. Trả về
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // 1. Yêu cầu Reset (Gửi OTP)
    public String forgotPassword(String email) {
        // Check user tồn tại
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Sinh OTP và lưu Redis (3 phút)
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        redisTemplate.opsForValue().set("otp:" + email, otp, Duration.ofMinutes(3));

        // Gửi mail
        emailServiceImpl.sendOtp(email, otp); // Bạn có thể sửa tiêu đề mail thành "Mã đặt lại mật khẩu"

        return "Mã xác thực đã được gửi đến email của bạn.";
    }

    // 2. Thực hiện đổi mật khẩu
    public String resetPassword(ResetPasswordRequest request) {
        String redisKey = "otp:" + request.getEmail();
        String cachedOtp = redisTemplate.opsForValue().get(redisKey);

        // Validate OTP
        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        // Lấy user và cập nhật pass mới
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP
        redisTemplate.delete(redisKey);

        return "Đổi mật khẩu thành công. Bạn có thể đăng nhập ngay.";
    }
}
