package com.zidioconnect.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
        logger.info("=== Profile Picture Upload Debug ===");
        logger.info("File name: {}", file.getOriginalFilename());
        logger.info("File size: {} bytes", file.getSize());
        logger.info("Content type: {}", file.getContentType());
        logger.info("File is empty: {}", file.isEmpty());

        if (file == null || file.isEmpty()) {
            logger.error("File is null or empty");
            throw new IllegalArgumentException("Profile picture file cannot be null or empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        logger.info("Validating content type: {}", contentType);

        if (contentType == null || !contentType.startsWith("image/")) {
            logger.error("Invalid content type: {}", contentType);
            throw new IllegalArgumentException("File must be an image");
        }

        // Validate file size (max 5MB for profile pictures)
        if (file.getSize() > 5 * 1024 * 1024) {
            logger.error("File too large: {} bytes (max: 5MB)", file.getSize());
            throw new IllegalArgumentException("Profile picture size must be less than 5MB");
        }

        logger.info("File validation passed, proceeding with upload...");

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
     * Upload document to Cloudinary based on type
     * 
     * @param file The file to upload
     * @param type The type of document (resume, marksheet, identity_proof,
     *             certificate)
     * @return Cloudinary URL of the uploaded file
     */
    public String uploadDocument(MultipartFile file, String type) throws IOException {
        logger.info("=== Document Upload Debug ===");
        logger.info("File name: {}", file != null ? file.getOriginalFilename() : "null");
        logger.info("File size: {} bytes", file != null ? file.getSize() : "N/A");
        logger.info("Content type: {}", file != null ? file.getContentType() : "null");
        logger.info("Document type: {}", type);
        
        if (file == null || file.isEmpty()) {
            logger.error("File is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Validate file type based on document type
        String contentType = file.getContentType();
        logger.info("Validating content type: {} for type: {}", contentType, type);
        
        if (contentType == null) {
            logger.error("Content type is null");
            throw new IllegalArgumentException("Content type cannot be null");
        }

        // Validate file size (max 10MB for all documents)
        if (file.getSize() > 10 * 1024 * 1024) {
            logger.error("File too large: {} bytes (max: 10MB)", file.getSize());
            throw new IllegalArgumentException("File size must be less than 10MB");
        }

        logger.info("File validation passed, proceeding with upload...");

        try {
            Map<String, Object> uploadOptions = new HashMap<>();

            switch (type) {
                case "profile_picture":
                    if (!contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("Profile picture must be an image file");
                    }
                    uploadOptions = ObjectUtils.asMap(
                            "folder", "zidioconnect/profile-pictures",
                            "transformation", "c_fill,g_face,w_400,h_400,q_auto",
                            "resource_type", "image");
                    break;

                case "resume":
                    if (!contentType.equals("application/pdf")) {
                        throw new IllegalArgumentException("Resume must be a PDF file");
                    }
                    uploadOptions = ObjectUtils.asMap(
                            "folder", "zidioconnect/documents/resumes",
                            "resource_type", "raw",
                            "format", "pdf");
                    break;

                case "marksheet":
                    if (!contentType.equals("application/pdf") &&
                            !contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("Marksheet must be a PDF or image file");
                    }
                    uploadOptions = ObjectUtils.asMap(
                            "folder", "zidioconnect/documents/marksheets",
                            "resource_type", contentType.equals("application/pdf") ? "raw" : "image",
                            "format", contentType.equals("application/pdf") ? "pdf" : "auto");
                    break;

                case "identity_proof":
                    if (!contentType.equals("application/pdf") &&
                            !contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("Identity proof must be a PDF or image file");
                    }
                    uploadOptions = ObjectUtils.asMap(
                            "folder", "zidioconnect/documents/identity-proofs",
                            "resource_type", contentType.equals("application/pdf") ? "raw" : "image",
                            "format", contentType.equals("application/pdf") ? "pdf" : "auto");
                    break;

                case "certificate":
                    if (!contentType.equals("application/pdf") &&
                            !contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("Certificate must be a PDF or image file");
                    }
                    uploadOptions = ObjectUtils.asMap(
                            "folder", "zidioconnect/documents/certificates",
                            "resource_type", contentType.equals("application/pdf") ? "raw" : "image",
                            "format", contentType.equals("application/pdf") ? "pdf" : "auto");
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported document type: " + type);
            }

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String fileUrl = (String) result.get("secure_url");

            logger.info("Document uploaded successfully: {} (type: {})", fileUrl, type);
            return fileUrl;
        } catch (IOException e) {
            logger.error("Error uploading document: ", e);
            throw new IOException("Failed to upload document", e);
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
        logger.info("=== Profile Picture Validation Debug ===");
        logger.info("File name: {}", file != null ? file.getOriginalFilename() : "null");
        logger.info("File is null: {}", file == null);
        logger.info("File is empty: {}", file != null ? file.isEmpty() : "N/A");

        if (file == null || file.isEmpty()) {
            logger.error("File is null or empty");
            return false;
        }

        String contentType = file.getContentType();
        logger.info("Content type: {}", contentType);

        boolean isValid = contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp"));

        logger.info("Validation result: {}", isValid);
        return isValid;
    }

    /**
     * Validate file type for documents
     * 
     * @param file The file to validate
     * @param type The document type
     * @return true if valid
     */
    public boolean isValidDocument(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        switch (type) {
            case "profile_picture":
                return contentType.startsWith("image/");
            case "resume":
                return contentType.equals("application/pdf");
            case "marksheet":
            case "identity_proof":
            case "certificate":
                return contentType.equals("application/pdf") ||
                        contentType.startsWith("image/");
            default:
                return false;
        }
    }
}