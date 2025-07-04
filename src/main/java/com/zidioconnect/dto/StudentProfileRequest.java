package com.zidioconnect.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class StudentProfileRequest {
    public String firstName;
    public String lastName;
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
    public Date dateOfBirth;
    public String address;
    public String bio;
    public String careerGoals;
    public List<String> skills;
    public List<String> interests;
    public List<String> preferredJobRoles;
    public List<String> preferredLocations;
}