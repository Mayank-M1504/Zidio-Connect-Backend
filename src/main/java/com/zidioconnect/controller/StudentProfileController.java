package com.zidioconnect.controller;

import com.zidioconnect.dto.StudentProfileRequest;
import com.zidioconnect.dto.StudentProfileResponse;
import com.zidioconnect.service.StudentProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/student")
public class StudentProfileController {
    private static final Logger logger = LoggerFactory.getLogger(StudentProfileController.class);

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private HttpServletRequest request;

    /**
     * Create or update student profile with files
     */
    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<?> createOrUpdateProfile(
            Authentication authentication,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "profileData", required = false) StudentProfileRequest profileData) {

        logger.info("Received Content-Type: {}", request.getContentType());
        try {
            String userEmail = authentication.getName();
            StudentProfileResponse response = studentProfileService.createOrUpdateProfile(
                    userEmail, profileData, profilePicture, resume);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating/updating profile: ", e);
            return ResponseEntity.internalServerError().body("An error occurred while processing your request");
        }
    }

    /**
     * Create or update student profile without files (JSON only)
     */
    @PostMapping(value = "/profile", consumes = "application/json")
    public ResponseEntity<?> createOrUpdateProfileJson(
            Authentication authentication,
            @RequestBody StudentProfileRequest profileData) {

        try {
            String userEmail = authentication.getName();
            StudentProfileResponse response = studentProfileService.createOrUpdateProfile(
                    userEmail, profileData, null, null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating/updating profile: ", e);
            return ResponseEntity.internalServerError().body("An error occurred while processing your request");
        }
    }

    /**
     * Get current user's profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            StudentProfileResponse response = studentProfileService.getProfile(userEmail);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Profile not found: ", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving profile: ", e);
            return ResponseEntity.internalServerError().body("An error occurred while retrieving your profile");
        }
    }

    /**
     * Get profile by user ID (for recruiters/admins)
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfileById(@PathVariable Long userId) {
        try {
            StudentProfileResponse response = studentProfileService.getProfileById(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Profile not found for user ID {}: ", userId, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving profile for user ID {}: ", userId, e);
            return ResponseEntity.internalServerError().body("An error occurred while retrieving the profile");
        }
    }

    /**
     * Delete current user's profile
     */
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            studentProfileService.deleteProfile(userEmail);
            return ResponseEntity.ok().body("Profile deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Profile not found: ", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting profile: ", e);
            return ResponseEntity.internalServerError().body("An error occurred while deleting your profile");
        }
    }

    /**
     * Update only profile picture
     */
    @PutMapping("/profile/picture")
    public ResponseEntity<?> updateProfilePicture(
            Authentication authentication,
            @RequestParam("profilePicture") MultipartFile profilePicture) {

        try {
            String userEmail = authentication.getName();
            String imageUrl = studentProfileService.updateProfilePicture(userEmail, profilePicture);
            return ResponseEntity.ok().body("{\"profilePicture\": \"" + imageUrl + "\"}");
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating profile picture: ", e);
            return ResponseEntity.internalServerError().body("An error occurred while updating your profile picture");
        }
    }

    /**
     * Update only resume
     */
    @PutMapping("/profile/resume")
    public ResponseEntity<?> updateResume(
            Authentication authentication,
            @RequestParam("resume") MultipartFile resume) {

        try {
            String userEmail = authentication.getName();
            String resumeUrl = studentProfileService.updateResume(userEmail, resume);
            return ResponseEntity.ok().body("{\"resume\": \"" + resumeUrl + "\"}");
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating resume: ", e);
            return ResponseEntity.internalServerError().body("An error occurred while updating your resume");
        }
    }
}