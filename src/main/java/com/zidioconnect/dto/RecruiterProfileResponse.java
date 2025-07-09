package com.zidioconnect.dto;

public class RecruiterProfileResponse {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String phone;
    public String companyName;
    public String companyWebsite;
    public String companyAddress;
    public String companyDescription;
    public String recruiterRole;
    public String linkedinProfile;
    public String stinNumber;
}