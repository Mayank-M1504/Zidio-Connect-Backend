package com.zidioconnect.service;

import com.zidioconnect.dto.StudentProfileRequest;
import com.zidioconnect.dto.StudentProfileResponse;
import com.zidioconnect.model.*;
import com.zidioconnect.repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentProfileService {
    @Autowired
    private StudentProfileRepository profileRepo;

    public StudentProfileResponse getProfileByStudentId(Long studentId) {
        StudentProfile profile = profileRepo.findByStudentId(studentId).orElse(null);
        return profile != null ? toResponse(profile) : null;
    }

    public StudentProfileResponse getProfileByEmail(String email) {
        StudentProfile profile = profileRepo.findByEmail(email).orElse(null);
        return profile != null ? toResponse(profile) : null;
    }

    @Transactional
    public StudentProfileResponse createOrUpdateProfile(Long studentId, StudentProfileRequest req, Student student) {
        StudentProfile profile = profileRepo.findByStudentId(studentId).orElse(new StudentProfile());
        profile.setStudent(student);
        profile.setFirstName(req.firstName);
        profile.setLastName(req.lastName);
        profile.setEmail(req.email);
        profile.setPhone(req.phone);
        profile.setCollege(req.college);
        profile.setCourse(req.course);
        profile.setYearOfStudy(req.yearOfStudy);
        profile.setGpa(req.gpa);
        profile.setExpectedGraduation(req.expectedGraduation);
        profile.setAcademicAchievements(req.academicAchievements);
        profile.setLinkedinProfile(req.linkedinProfile);
        profile.setGithubProfile(req.githubProfile);
        profile.setPortfolioWebsite(req.portfolioWebsite);
        profile.setDateOfBirth(req.dateOfBirth);
        profile.setAddress(req.address);
        profile.setBio(req.bio);
        profile.setCareerGoals(req.careerGoals);

        // Skills
        profile.getSkills().clear();
        if (req.skills != null) {
            for (String skill : req.skills) {
                StudentProfileSkill s = new StudentProfileSkill();
                s.setSkill(skill);
                s.setProfile(profile);
                profile.getSkills().add(s);
            }
        }
        // Interests
        profile.getInterests().clear();
        if (req.interests != null) {
            for (String interest : req.interests) {
                StudentProfileInterest i = new StudentProfileInterest();
                i.setInterest(interest);
                i.setProfile(profile);
                profile.getInterests().add(i);
            }
        }
        // Job Roles
        profile.getPreferredJobRoles().clear();
        if (req.preferredJobRoles != null) {
            for (String role : req.preferredJobRoles) {
                StudentProfileJobRole r = new StudentProfileJobRole();
                r.setJobRole(role);
                r.setProfile(profile);
                profile.getPreferredJobRoles().add(r);
            }
        }
        // Locations
        profile.getPreferredLocations().clear();
        if (req.preferredLocations != null) {
            for (String loc : req.preferredLocations) {
                StudentProfileLocation l = new StudentProfileLocation();
                l.setLocation(loc);
                l.setProfile(profile);
                profile.getPreferredLocations().add(l);
            }
        }
        profile = profileRepo.save(profile);
        return toResponse(profile);
    }

    private StudentProfileResponse toResponse(StudentProfile profile) {
        StudentProfileResponse resp = new StudentProfileResponse();
        resp.id = profile.getId();
        resp.firstName = profile.getFirstName();
        resp.lastName = profile.getLastName();
        resp.email = profile.getEmail();
        resp.phone = profile.getPhone();
        resp.college = profile.getCollege();
        resp.course = profile.getCourse();
        resp.yearOfStudy = profile.getYearOfStudy();
        resp.gpa = profile.getGpa();
        resp.expectedGraduation = profile.getExpectedGraduation();
        resp.academicAchievements = profile.getAcademicAchievements();
        resp.linkedinProfile = profile.getLinkedinProfile();
        resp.githubProfile = profile.getGithubProfile();
        resp.portfolioWebsite = profile.getPortfolioWebsite();
        resp.dateOfBirth = profile.getDateOfBirth();
        resp.address = profile.getAddress();
        resp.bio = profile.getBio();
        resp.careerGoals = profile.getCareerGoals();
        resp.skills = profile.getSkills().stream().map(StudentProfileSkill::getSkill).collect(Collectors.toList());
        resp.interests = profile.getInterests().stream().map(StudentProfileInterest::getInterest)
                .collect(Collectors.toList());
        resp.preferredJobRoles = profile.getPreferredJobRoles().stream().map(StudentProfileJobRole::getJobRole)
                .collect(Collectors.toList());
        resp.preferredLocations = profile.getPreferredLocations().stream().map(StudentProfileLocation::getLocation)
                .collect(Collectors.toList());
        return resp;
    }
}