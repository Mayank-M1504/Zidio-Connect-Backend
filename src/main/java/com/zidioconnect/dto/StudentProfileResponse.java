package com.zidioconnect.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentProfileResponse {
    private Long id;
    private Long userId;
    private String userEmail;

    // Basic Information
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String phone;
    private String address;
    private String bio;

    // Academic Information
    private String college;
    private String course;
    private Integer currentYear;
    private String expectedGraduationDate;
    private Double gpa;
    private String major;
    private String minor;

    // Professional Information
    private String linkedinProfile;
    private String githubProfile;
    private String portfolioUrl;

    // Files
    private String profilePicture;
    private String resume;

    // Career Information
    private String careerGoals;
    private String workAuthorizationStatus;
    private Boolean willingToRelocate;
    private Integer salaryExpectations;
    private String availabilityDate;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}