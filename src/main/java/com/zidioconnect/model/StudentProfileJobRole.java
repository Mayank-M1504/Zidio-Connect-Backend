package com.zidioconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_profile_job_role")
public class StudentProfileJobRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile profile;

    @Column(nullable = false)
    private String jobRole;

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public StudentProfile getProfile() {
        return profile;
    }

    public void setProfile(StudentProfile profile) {
        this.profile = profile;
    }

    // Getters and setters omitted for brevity
}