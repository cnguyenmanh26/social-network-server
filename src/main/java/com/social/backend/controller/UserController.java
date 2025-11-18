package com.social.backend.controller;

import com.social.backend.dto.ApiResponse;
import com.social.backend.dto.UpdateProfileRequest;
import com.social.backend.dto.UserResponse;
import com.social.backend.service.UserService;
import com.social.backend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userServiceImpl;

    // Lấy thông tin chính mình
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile() {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.getMyInfo())
                .build();
    }

    @PutMapping(value = "/me", consumes = "multipart/form-data") // Bắt buộc phải có consumes
    public ApiResponse<UserResponse> updateProfile(
            @ModelAttribute UpdateProfileRequest request, // Hứng các field text (name, bio...)
            @RequestPart(name = "avatar", required = false) MultipartFile avatar // Hứng file ảnh
    ) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.updateProfile(request, avatar))
                .build();
    }

    // Xem thông tin người khác (Theo ID)
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserProfile(@PathVariable Long userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.getUserById(userId))
                .build();
    }
}
