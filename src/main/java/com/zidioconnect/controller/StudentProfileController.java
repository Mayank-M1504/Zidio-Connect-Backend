package com.zidioconnect.controller;

import com.zidioconnect.dto.StudentProfileRequest;
import com.zidioconnect.dto.StudentProfileResponse;
import com.zidioconnect.model.Student;
import com.zidioconnect.repository.StudentRepository;
import com.zidioconnect.service.StudentProfileService;
import com.zidioconnect.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class StudentProfileController {
    @Autowired
    private StudentProfileService profileService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<StudentProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        StudentProfileResponse resp = profileService.getProfileByEmail(email);
        if (resp == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<StudentProfileResponse> createOrUpdateProfile(Authentication authentication,
            @RequestBody StudentProfileRequest req) {
        String email = authentication.getName();
        Student student = studentRepository.findByEmail(email).orElse(null);
        if (student == null)
            return ResponseEntity.badRequest().build();
        StudentProfileResponse resp = profileService.createOrUpdateProfile(student.getId(), req, student);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadProfilePhoto(Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        String email = authentication.getName();
        Student student = studentRepository.findByEmail(email).orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        try {
            String url = fileUploadService.uploadProfilePicture(file);
            student.setProfilePicture(url);
            studentRepository.save(student);
            return ResponseEntity.ok().body(java.util.Map.of("profilePictureUrl", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload profile picture: " + e.getMessage());
        }
    }
}