package com.zidioconnect.service;

import com.zidioconnect.dto.StudentProfileRequest;
import com.zidioconnect.dto.StudentProfileResponse;
import com.zidioconnect.model.StudentProfile;
import com.zidioconnect.model.User;
import com.zidioconnect.repository.StudentProfileRepository;
import com.zidioconnect.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class StudentProfileService {
    private static final Logger logger = LoggerFactory.getLogger(StudentProfileService.class);

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * Create or update student profile
     */
    public StudentProfileResponse createOrUpdateProfile(String userEmail, StudentProfileRequest request,
            MultipartFile profilePicture, MultipartFile resume) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getRole().name().equals("STUDENT")) {
            throw new IllegalArgumentException("Only students can create profiles");
        }

        Optional<StudentProfile> existingProfile = studentProfileRepository.findByUserId(user.getId());
        StudentProfile profile;

        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
            // Delete old files if new ones are provided
            if (profilePicture != null && !profilePicture.isEmpty()) {
                deleteOldProfilePicture(profile.getProfilePicture());
            }
            if (resume != null && !resume.isEmpty()) {
                deleteOldResume(profile.getResume());
            }
        } else {
            profile = new StudentProfile();
            profile.setUser(user);
        }

        // Upload new files if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String profilePictureUrl = fileUploadService.uploadProfilePicture(profilePicture);
            profile.setProfilePicture(profilePictureUrl);
        }

        if (resume != null && !resume.isEmpty()) {
            String resumeUrl = fileUploadService.uploadResume(resume);
            profile.setResume(resumeUrl);
        }

        // Update profile data
        updateProfileData(profile, request);

        StudentProfile savedProfile = studentProfileRepository.save(profile);
        return convertToResponse(savedProfile);
    }

    /**
     * Get student profile by user email
     */
    public StudentProfileResponse getProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return convertToResponse(profile);
    }

    /**
     * Get student profile by user ID (for recruiters/admins)
     */
    public StudentProfileResponse getProfileById(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return convertToResponse(profile);
    }

    /**
     * Delete student profile
     */
    public void deleteProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Delete files from Cloudinary
        deleteOldProfilePicture(profile.getProfilePicture());
        deleteOldResume(profile.getResume());

        studentProfileRepository.delete(profile);
    }

    /**
     * Update only profile picture
     */
    public String updateProfilePicture(String userEmail, MultipartFile profilePicture) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Delete old profile picture
        deleteOldProfilePicture(profile.getProfilePicture());

        // Upload new profile picture
        String profilePictureUrl = fileUploadService.uploadProfilePicture(profilePicture);
        profile.setProfilePicture(profilePictureUrl);
        studentProfileRepository.save(profile);

        return profilePictureUrl;
    }

    /**
     * Update only resume
     */
    public String updateResume(String userEmail, MultipartFile resume) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Delete old resume
        deleteOldResume(profile.getResume());

        // Upload new resume
        String resumeUrl = fileUploadService.uploadResume(resume);
        profile.setResume(resumeUrl);
        studentProfileRepository.save(profile);

        return resumeUrl;
    }

    /**
     * Update profile data from request
     */
    private void updateProfileData(StudentProfile profile, StudentProfileRequest request) {
        // Basic Information
        if (request.getFirstName() != null)
            profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            profile.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null)
            profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)
            profile.setGender(request.getGender());
        if (request.getNationality() != null)
            profile.setNationality(request.getNationality());
        if (request.getPhone() != null)
            profile.setPhone(request.getPhone());
        if (request.getAddress() != null)
            profile.setAddress(request.getAddress());
        if (request.getBio() != null)
            profile.setBio(request.getBio());

        // Academic Information
        if (request.getCollege() != null)
            profile.setCollege(request.getCollege());
        if (request.getCourse() != null)
            profile.setCourse(request.getCourse());
        if (request.getCurrentYear() != null)
            profile.setCurrentYear(request.getCurrentYear());
        if (request.getExpectedGraduationDate() != null)
            profile.setExpectedGraduationDate(request.getExpectedGraduationDate());
        if (request.getGpa() != null)
            profile.setGpa(request.getGpa());
        if (request.getMajor() != null)
            profile.setMajor(request.getMajor());
        if (request.getMinor() != null)
            profile.setMinor(request.getMinor());

        // Professional Information
        if (request.getLinkedinProfile() != null)
            profile.setLinkedinProfile(request.getLinkedinProfile());
        if (request.getGithubProfile() != null)
            profile.setGithubProfile(request.getGithubProfile());
        if (request.getPortfolioUrl() != null)
            profile.setPortfolioUrl(request.getPortfolioUrl());

        // Career Information
        if (request.getCareerGoals() != null)
            profile.setCareerGoals(request.getCareerGoals());
        if (request.getWorkAuthorizationStatus() != null)
            profile.setWorkAuthorizationStatus(request.getWorkAuthorizationStatus());
        if (request.getWillingToRelocate() != null)
            profile.setWillingToRelocate(request.getWillingToRelocate());
        if (request.getSalaryExpectations() != null)
            profile.setSalaryExpectations(request.getSalaryExpectations());
        if (request.getAvailabilityDate() != null)
            profile.setAvailabilityDate(request.getAvailabilityDate());
    }

    /**
     * Convert StudentProfile to StudentProfileResponse
     */
    private StudentProfileResponse convertToResponse(StudentProfile profile) {
        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setUserEmail(profile.getUser().getEmail());

        // Basic Information
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setGender(profile.getGender());
        response.setNationality(profile.getNationality());
        response.setPhone(profile.getPhone());
        response.setAddress(profile.getAddress());
        response.setBio(profile.getBio());

        // Academic Information
        response.setCollege(profile.getCollege());
        response.setCourse(profile.getCourse());
        response.setCurrentYear(profile.getCurrentYear());
        response.setExpectedGraduationDate(profile.getExpectedGraduationDate());
        response.setGpa(profile.getGpa());
        response.setMajor(profile.getMajor());
        response.setMinor(profile.getMinor());

        // Professional Information
        response.setLinkedinProfile(profile.getLinkedinProfile());
        response.setGithubProfile(profile.getGithubProfile());
        response.setPortfolioUrl(profile.getPortfolioUrl());

        // Files
        response.setProfilePicture(profile.getProfilePicture());
        response.setResume(profile.getResume());

        // Career Information
        response.setCareerGoals(profile.getCareerGoals());
        response.setWorkAuthorizationStatus(profile.getWorkAuthorizationStatus());
        response.setWillingToRelocate(profile.getWillingToRelocate());
        response.setSalaryExpectations(profile.getSalaryExpectations());
        response.setAvailabilityDate(profile.getAvailabilityDate());

        // Timestamps
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        return response;
    }

    /**
     * Delete old profile picture from Cloudinary
     */
    private void deleteOldProfilePicture(String profilePictureUrl) {
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            String publicId = fileUploadService.extractPublicIdFromUrl(profilePictureUrl);
            if (publicId != null) {
                fileUploadService.deleteFile(publicId, "image");
            }
        }
    }

    /**
     * Delete old resume from Cloudinary
     */
    private void deleteOldResume(String resumeUrl) {
        if (resumeUrl != null && !resumeUrl.isEmpty()) {
            String publicId = fileUploadService.extractPublicIdFromUrl(resumeUrl);
            if (publicId != null) {
                fileUploadService.deleteFile(publicId, "raw");
            }
        }
    }
}