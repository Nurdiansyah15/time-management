package com.tunduh.timemanagement.service.impl;

import com.cloudinary.Cloudinary;

import com.tunduh.timemanagement.service.CloudinaryService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    @Resource
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("folder", folderName);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadResult.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Image invalid");
        }
    }

    @Override
    public String updateFile(String oldUrl, MultipartFile newFile, String folderName) {
        String publicId = extractPublicIdFromUrl(oldUrl);
        deleteImage(oldUrl);
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("folder", folderName);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(newFile.getBytes(), options);
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteImage(String url) {
        String publicId = extractPublicIdFromUrl(url);
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("folder", "user_profile");
            cloudinary.uploader().destroy(publicId, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }
}
