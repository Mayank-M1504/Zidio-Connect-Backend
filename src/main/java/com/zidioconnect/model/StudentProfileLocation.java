package com.zidioconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_profile_location")
public class StudentProfileLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile profile;

    @Column(nullable = false)
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StudentProfile getProfile() {
        return profile;
    }

    public void setProfile(StudentProfile profile) {
        this.profile = profile;
    }

    // Getters and setters omitted for brevity
}