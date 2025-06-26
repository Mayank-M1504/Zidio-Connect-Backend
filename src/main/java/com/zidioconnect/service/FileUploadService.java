package com.zidioconnect.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload profile picture to Cloudinary
     * 
     * @param file The image file to upload
     * @return Cloudinary URL of the uploaded image
     */
    public String uploadProfilePicture(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile picture file cannot be null or empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Validate file size (max 5MB for profile pictures)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Profile picture size must be less than 5MB");
        }

        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", "zidioconnect/profile-pictures",
                    "transformation", "c_fill,g_face,w_400,h_400,q_auto",
                    "resource_type", "image");

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String imageUrl = (String) result.get("secure_url");

            logger.info("Profile picture uploaded successfully: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            logger.error("Error uploading profile picture: ", e);
            throw new IOException("Failed to upload profile picture", e);
        }
    }

    /**
     * Upload resume PDF to Cloudinary
     * 
     * @param file The PDF file to upload
     * @return Cloudinary URL of the uploaded PDF
     */
    public String uploadResume(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file cannot be null or empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Resume must be a PDF file");
        }

        // Validate file size (max 10MB for resumes)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Resume size must be less than 10MB");
        }

        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", "zidioconnect/resumes",
                    "resource_type", "raw",
                    "format", "pdf");

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String pdfUrl = (String) result.get("secure_url");

            logger.info("Resume uploaded successfully: {}", pdfUrl);
            return pdfUrl;
        } catch (IOException e) {
            logger.error("Error uploading resume: ", e);
            throw new IOException("Failed to upload resume", e);
        }
    }

    /**
     * Delete file from Cloudinary
     * 
     * @param publicId     The public ID of the file to delete
     * @param resourceType The resource type (image, raw, etc.)
     * @return true if deletion was successful
     */
    public boolean deleteFile(String publicId, String resourceType) {
        try {
            Map<String, Object> deleteOptions = ObjectUtils.asMap(
                    "resource_type", resourceType);

            Map<String, Object> result = cloudinary.uploader().destroy(publicId, deleteOptions);
            String resultStatus = (String) result.get("result");

            boolean success = "ok".equals(resultStatus);
            if (success) {
                logger.info("File deleted successfully: {}", publicId);
            } else {
                logger.warn("File deletion failed: {}", publicId);
            }

            return success;
        } catch (IOException e) {
            logger.error("Error deleting file: ", e);
            return false;
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * 
     * @param url The Cloudinary URL
     * @return The public ID
     */
    public String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Extract public ID from URL like:
            // https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/filename.jpg
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String[] uploadParts = parts[1].split("/");
                if (uploadParts.length > 1) {
                    // Skip version number and get the rest
                    StringBuilder publicId = new StringBuilder();
                    for (int i = 1; i < uploadParts.length; i++) {
                        if (i > 1)
                            publicId.append("/");
                        publicId.append(uploadParts[i]);
                    }
                    // Remove file extension
                    String result = publicId.toString();
                    int lastDotIndex = result.lastIndexOf(".");
                    if (lastDotIndex > 0) {
                        result = result.substring(0, lastDotIndex);
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting public ID from URL: {}", url, e);
        }

        return null;
    }

    /**
     * Validate file type for profile picture
     * 
     * @param file The file to validate
     * @return true if valid
     */
    public boolean isValidProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp"));
    }

    /**
     * Validate file type for resume
     * 
     * @param file The file to validate
     * @return true if valid
     */
    public boolean isValidResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
}