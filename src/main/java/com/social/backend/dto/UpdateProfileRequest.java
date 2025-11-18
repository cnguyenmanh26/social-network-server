package com.social.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String bio;
    private String avatarUrl; // Tạm thời nhận link ảnh (sau này làm upload file sẽ trả link về đây)
    private LocalDate dob; // Ngày sinh
    // Không cho phép update Email/Username ở đây vì liên quan đến định danh
}