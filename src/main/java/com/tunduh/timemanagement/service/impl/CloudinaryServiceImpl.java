package com.tunduh.timemanagement.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            Map<String, String> params = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto"
            );
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String publicId = (String) uploadResult.get("public_id");
            String url = cloudinary.url().secure(true).generate(publicId);
            logger.info("File uploaded successfully to Cloudinary. Public ID: {}", publicId);
            return url;
        } catch (IOException e) {
            logger.error("Error uploading file to Cloudinary", e);
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    public void deleteFile(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("File deleted from Cloudinary. Public ID: {}", publicId);
        } catch (IOException e) {
            logger.error("Error deleting file from Cloudinary", e);
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }
}