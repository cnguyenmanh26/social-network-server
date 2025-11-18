package com.social.backend.service;

import com.social.backend.dto.UpdateProfileRequest;
import com.social.backend.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse getMyInfo();
    UserResponse updateProfile(UpdateProfileRequest request, MultipartFile avatarFile);
    UserResponse getUserById(Long id);
}
