package com.zidioconnect.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "student_profile")
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", unique = true, nullable = false)
    private Student student;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phone;
    private String college;
    private String course;
    private String yearOfStudy;
    private String gpa;
    private Date expectedGraduation;

    @Column(columnDefinition = "TEXT")
    private String academicAchievements;

    private String linkedinProfile;
    private String githubProfile;
    private String portfolioWebsite;
    private Date dateOfBirth;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String careerGoals;

    // Collections
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentProfileSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentProfileInterest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentProfileJobRole> preferredJobRoles = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentProfileLocation> preferredLocations = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentDocument> documents = new ArrayList<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public Date getExpectedGraduation() {
        return expectedGraduation;
    }

    public void setExpectedGraduation(Date expectedGraduation) {
        this.expectedGraduation = expectedGraduation;
    }

    public String getAcademicAchievements() {
        return academicAchievements;
    }

    public void setAcademicAchievements(String academicAchievements) {
        this.academicAchievements = academicAchievements;
    }

    public String getLinkedinProfile() {
        return linkedinProfile;
    }

    public void setLinkedinProfile(String linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
    }

    public String getGithubProfile() {
        return githubProfile;
    }

    public void setGithubProfile(String githubProfile) {
        this.githubProfile = githubProfile;
    }

    public String getPortfolioWebsite() {
        return portfolioWebsite;
    }

    public void setPortfolioWebsite(String portfolioWebsite) {
        this.portfolioWebsite = portfolioWebsite;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCareerGoals() {
        return careerGoals;
    }

    public void setCareerGoals(String careerGoals) {
        this.careerGoals = careerGoals;
    }

    public List<StudentProfileSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<StudentProfileSkill> skills) {
        this.skills = skills;
    }

    public List<StudentProfileInterest> getInterests() {
        return interests;
    }

    public void setInterests(List<StudentProfileInterest> interests) {
        this.interests = interests;
    }

    public List<StudentProfileJobRole> getPreferredJobRoles() {
        return preferredJobRoles;
    }

    public void setPreferredJobRoles(List<StudentProfileJobRole> preferredJobRoles) {
        this.preferredJobRoles = preferredJobRoles;
    }

    public List<StudentProfileLocation> getPreferredLocations() {
        return preferredLocations;
    }

    public void setPreferredLocations(List<StudentProfileLocation> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }

    public List<StudentDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<StudentDocument> documents) {
        this.documents = documents;
    }
}