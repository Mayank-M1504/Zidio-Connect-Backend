package com.zidioconnect.dto;

import java.util.List;

public class StudentProfileDTO {
    public Long id;
    public String name;
    public String email;
    public String phone;
    public String college;
    public String course;
    public String yearOfStudy;
    public String gpa;
    public String academicAchievements;
    public String linkedinProfile;
    public String githubProfile;
    public String portfolioWebsite;
    public String dateOfBirth;
    public String address;
    public String bio;
    public String careerGoals;
    public List<String> skills;
    public List<String> interests;
    public List<String> preferredJobRoles;
    public List<String> preferredLocations;
    public List<DocumentDTO> documents;
    public List<CertificateDTO> certificates;
}