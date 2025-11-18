package com.social.backend.service.impl;

import com.social.backend.dto.UpdateProfileRequest;
import com.social.backend.dto.UserResponse;
import com.social.backend.entity.User;
import com.social.backend.exception.AppException;
import com.social.backend.exception.ErrorCode;
import com.social.backend.repository.UserRepository;
import com.social.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service // Spring sẽ quản lý Bean này
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // 1. Lấy thông tin của chính mình (My Profile)
    public UserResponse getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request, MultipartFile avatarFile) {
        // 2. Cập nhật thông tin

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Map thủ công hoặc dùng Mapper (Ở đây map thủ công cho bạn dễ hiểu)
            if (request.getFullName() != null) user.setFullName(request.getFullName());
            if (request.getBio() != null) user.setBio(request.getBio());
            if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

            return toUserResponse(userRepository.save(user));
        }




    // 3. Xem profile người khác (Public Profile)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return toUserResponse(user);
    }

    // Helper chuyển Entity -> Response
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}