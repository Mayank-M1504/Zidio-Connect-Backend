package com.zidioconnect.service;

import com.zidioconnect.dto.RecruiterProfileRequest;
import com.zidioconnect.dto.RecruiterProfileResponse;
import com.zidioconnect.model.RecruiterProfile;
import com.zidioconnect.repository.RecruiterProfileRepository;
import com.zidioconnect.model.Recruiter;
import com.zidioconnect.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecruiterProfileService {
    @Autowired
    private RecruiterProfileRepository profileRepo;
    @Autowired
    private RecruiterRepository recruiterRepository;

    public RecruiterProfileResponse getProfileByEmail(String email) {
        RecruiterProfile profile = profileRepo.findByEmail(email).orElse(null);
        return profile != null ? toResponse(profile) : null;
    }

    @Transactional
    public RecruiterProfileResponse createOrUpdateProfile(RecruiterProfileRequest req) {
        RecruiterProfile profile = profileRepo.findByEmail(req.email).orElse(new RecruiterProfile());
        profile.setFirstName(req.firstName);
        profile.setLastName(req.lastName);
        profile.setEmail(req.email);
        profile.setPhone(req.phone);
        profile.setCompanyName(req.companyName);
        profile.setCompanyWebsite(req.companyWebsite);
        profile.setCompanyAddress(req.companyAddress);
        profile.setCompanyDescription(req.companyDescription);
        profile.setRecruiterRole(req.recruiterRole);
        profile.setLinkedinProfile(req.linkedinProfile);
        profile.setStinNumber(req.stinNumber);
        profile = profileRepo.save(profile);

        // Sync company name and phone number to Recruiter entity for job posting
        // validation
        Recruiter recruiter = recruiterRepository.findByEmail(req.email).orElse(null);
        if (recruiter != null) {
            recruiter.setCompany(req.companyName);
            recruiter.setPhoneNumber(req.phone); // Sync phone number
            recruiterRepository.save(recruiter);
        }

        return toResponse(profile);
    }

    private RecruiterProfileResponse toResponse(RecruiterProfile profile) {
        RecruiterProfileResponse resp = new RecruiterProfileResponse();
        resp.id = profile.getId();
        resp.firstName = profile.getFirstName();
        resp.lastName = profile.getLastName();
        resp.email = profile.getEmail();
        resp.phone = profile.getPhone();
        resp.companyName = profile.getCompanyName();
        resp.companyWebsite = profile.getCompanyWebsite();
        resp.companyAddress = profile.getCompanyAddress();
        resp.companyDescription = profile.getCompanyDescription();
        resp.recruiterRole = profile.getRecruiterRole();
        resp.linkedinProfile = profile.getLinkedinProfile();
        resp.stinNumber = profile.getStinNumber();
        // Fetch companyLogo from Recruiter entity
        Recruiter recruiter = recruiterRepository.findByEmail(profile.getEmail()).orElse(null);
        resp.companyLogo = recruiter != null ? recruiter.getCompanyLogo() : null;
        return resp;
    }
}