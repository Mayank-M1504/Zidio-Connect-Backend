package com.zidioconnect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_profiles")
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}