package com.zidioconnect.controller;

import com.cloudinary.Cloudinary;
import com.zidioconnect.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private FileUploadService fileUploadService;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/auth")
    public Map<String, Object> getAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
            response.put("principal", authentication.getPrincipal().getClass().getSimpleName());

            // Log user login statistics
            // userService.logUserLoginStats(authentication.getName());
        } else {
            response.put("authenticated", false);
        }

        return response;
    }

    /**
     * Test Cloudinary configuration
     */
    @GetMapping("/cloudinary-config")
    public ResponseEntity<?> testCloudinaryConfig() {
        try {
            Map<String, Object> response = new HashMap<>();

            // Check if environment variables are loaded
            response.put("cloudName", cloudName != null ? cloudName : "NOT_SET");
            response.put("apiKey",
                    apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "NOT_SET");
            response.put("apiSecret",
                    apiSecret != null ? "***" + apiSecret.substring(Math.max(0, apiSecret.length() - 4)) : "NOT_SET");

            // Test Cloudinary connection
            boolean cloudinaryConnected = cloudinary != null;
            response.put("cloudinaryConnected", cloudinaryConnected);

            if (cloudinaryConnected) {
                response.put("status", "SUCCESS");
                response.put("message", "Cloudinary configuration is working correctly");
            } else {
                response.put("status", "ERROR");
                response.put("message", "Cloudinary is not properly configured");
            }

            logger.info("Cloudinary configuration test completed: {}", response.get("status"));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error testing Cloudinary configuration: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Error testing Cloudinary configuration: " + e.getMessage()));
        }
    }

    /**
     * Test file upload service
     */
    @PostMapping("/upload-test")
    public ResponseEntity<?> testFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> response = new HashMap<>();

            // Check file
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());

            // Test file validation
            boolean isValidImage = fileUploadService.isValidProfilePicture(file);
            boolean isValidPdf = fileUploadService.isValidDocument(file, "resume");

            response.put("isValidImage", isValidImage);
            response.put("isValidPdf", isValidPdf);

            if (isValidImage) {
                // Test profile picture upload
                String imageUrl = fileUploadService.uploadProfilePicture(file);
                response.put("uploadedImageUrl", imageUrl);
                response.put("uploadType", "profile_picture");
            } else if (isValidPdf) {
                // Test resume upload
                String pdfUrl = fileUploadService.uploadDocument(file, "resume");
                response.put("uploadedPdfUrl", pdfUrl);
                response.put("uploadType", "resume");
            } else {
                response.put("error", "Invalid file type. Expected image for profile picture or PDF for resume");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("status", "SUCCESS");
            response.put("message", "File uploaded successfully");

            logger.info("File upload test completed successfully for file: {}", file.getOriginalFilename());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error testing file upload: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Error testing file upload: " + e.getMessage()));
        }
    }

    /**
     * Test environment variables
     */
    @GetMapping("/env-test")
    public ResponseEntity<?> testEnvironmentVariables() {
        Map<String, Object> response = new HashMap<>();

        // Check all environment variables
        response.put("CLOUDINARY_CLOUD_NAME", System.getenv("CLOUDINARY_CLOUD_NAME"));
        response.put("CLOUDINARY_API_KEY", System.getenv("CLOUDINARY_API_KEY") != null ? "SET" : "NOT_SET");
        response.put("CLOUDINARY_API_SECRET", System.getenv("CLOUDINARY_API_SECRET") != null ? "SET" : "NOT_SET");

        // Check application properties values
        response.put("app_cloud_name", cloudName);
        response.put("app_api_key", apiKey != null ? "SET" : "NOT_SET");
        response.put("app_api_secret", apiSecret != null ? "SET" : "NOT_SET");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().body("Backend is running successfully!");
    }

    @PostMapping("/test-json")
    public ResponseEntity<?> testJson(@RequestBody Object data) {
        logger.info("Test JSON endpoint received: {}", data);
        return ResponseEntity.ok().body("JSON received successfully: " + data);
    }
}