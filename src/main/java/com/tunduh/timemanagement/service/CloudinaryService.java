package com.tunduh.timemanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName);

    String updateFile(String url, MultipartFile newFile, String folderName);

    void deleteImage(String url);
}