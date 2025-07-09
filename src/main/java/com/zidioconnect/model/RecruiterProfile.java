package com.zidioconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recruiter_profile")
public class RecruiterProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String companyName;
    private String companyWebsite;
    private String companyAddress;
    @Column(columnDefinition = "TEXT")
    private String companyDescription;
    private String recruiterRole;
    private String linkedinProfile;
    private String stinNumber;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }
    public String getRecruiterRole() { return recruiterRole; }
    public void setRecruiterRole(String recruiterRole) { this.recruiterRole = recruiterRole; }
    public String getLinkedinProfile() { return linkedinProfile; }
    public void setLinkedinProfile(String linkedinProfile) { this.linkedinProfile = linkedinProfile; }
    public String getStinNumber() { return stinNumber; }
    public void setStinNumber(String stinNumber) { this.stinNumber = stinNumber; }
} 