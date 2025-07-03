package com.zidioconnect.controller;

import com.zidioconnect.dto.StudentProfileRequest;
import com.zidioconnect.dto.StudentProfileResponse;
import com.zidioconnect.model.Student;
import com.zidioconnect.repository.StudentRepository;
import com.zidioconnect.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class StudentProfileController {
    @Autowired
    private StudentProfileService profileService;
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentProfileResponse> getProfile(@PathVariable Long studentId) {
        StudentProfileResponse resp = profileService.getProfileByStudentId(studentId);
        if (resp == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{studentId}")
    public ResponseEntity<StudentProfileResponse> createOrUpdateProfile(@PathVariable Long studentId,
            @RequestBody StudentProfileRequest req) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null)
            return ResponseEntity.badRequest().build();
        StudentProfileResponse resp = profileService.createOrUpdateProfile(studentId, req, student);
        return ResponseEntity.ok(resp);
    }
}