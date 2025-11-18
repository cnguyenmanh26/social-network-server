package com.social.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private boolean authenticated;
    // Có thể trả thêm thông tin user để Frontend đỡ phải gọi API lấy profile ngay lập tức
    // private String username;
    // private String avatarUrl;
}
