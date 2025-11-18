package com.social.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    // Upload file và trả về đường dẫn URL ảnh
    String uploadFile(MultipartFile file) throws IOException;
}
