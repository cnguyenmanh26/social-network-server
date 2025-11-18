package com.social.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class CloudinaryStorageImpl implements StorageService {
    private final Cloudinary cloudinary;


    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // upload(file.getBytes(), options)
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Trả về đường dẫn URL của ảnh trên Cloudinary
        return uploadResult.get("secure_url").toString();
    }
}
