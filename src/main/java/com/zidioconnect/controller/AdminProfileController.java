package com.zidioconnect.controller;

import com.zidioconnect.model.StudentProfile;
import com.zidioconnect.model.RecruiterProfile;
import com.zidioconnect.model.StudentDocument;
import com.zidioconnect.model.StudentCertificate;
import com.zidioconnect.model.RecruiterDocument;
import com.zidioconnect.repository.StudentProfileRepository;
import com.zidioconnect.repository.RecruiterProfileRepository;
import com.zidioconnect.repository.StudentDocumentRepository;
import com.zidioconnect.repository.StudentCertificateRepository;
import com.zidioconnect.repository.RecruiterDocumentRepository;
import com.zidioconnect.dto.StudentProfileDTO;
import com.zidioconnect.dto.RecruiterProfileDTO;
import com.zidioconnect.dto.DocumentDTO;
import com.zidioconnect.dto.CertificateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/profiles")
public class AdminProfileController {
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;
    @Autowired
    private StudentDocumentRepository studentDocumentRepository;
    @Autowired
    private StudentCertificateRepository studentCertificateRepository;
    @Autowired
    private RecruiterDocumentRepository recruiterDocumentRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllProfiles() {
        // Fetch all student profiles
        List<StudentProfile> studentProfiles = studentProfileRepository.findAll();
        // Fetch all recruiter profiles
        List<RecruiterProfile> recruiterProfiles = recruiterProfileRepository.findAll();

        // Map student profiles to DTOs with documents/certificates
        List<StudentProfileDTO> students = studentProfiles.stream().map(profile -> {
            StudentProfileDTO dto = new StudentProfileDTO();
            dto.id = profile.getId();
            dto.name = profile.getFirstName() + " " + profile.getLastName();
            dto.email = profile.getEmail();
            dto.college = profile.getCollege();
            dto.course = profile.getCourse();
            dto.yearOfStudy = profile.getYearOfStudy();
            dto.phone = profile.getPhone();
            dto.gpa = profile.getGpa();
            dto.academicAchievements = profile.getAcademicAchievements();
            dto.linkedinProfile = profile.getLinkedinProfile();
            dto.githubProfile = profile.getGithubProfile();
            dto.portfolioWebsite = profile.getPortfolioWebsite();
            dto.dateOfBirth = profile.getDateOfBirth() != null ? profile.getDateOfBirth().toString() : null;
            dto.address = profile.getAddress();
            dto.bio = profile.getBio();
            dto.careerGoals = profile.getCareerGoals();
            dto.skills = profile.getSkills() != null
                    ? profile.getSkills().stream().map(s -> s.getSkill()).collect(java.util.stream.Collectors.toList())
                    : java.util.Collections.emptyList();
            dto.interests = profile.getInterests() != null ? profile.getInterests().stream().map(i -> i.getInterest())
                    .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList();
            dto.preferredJobRoles = profile.getPreferredJobRoles() != null ? profile.getPreferredJobRoles().stream()
                    .map(j -> j.getJobRole()).collect(java.util.stream.Collectors.toList())
                    : java.util.Collections.emptyList();
            dto.preferredLocations = profile.getPreferredLocations() != null ? profile.getPreferredLocations().stream()
                    .map(l -> l.getLocation()).collect(java.util.stream.Collectors.toList())
                    : java.util.Collections.emptyList();
            dto.documents = studentDocumentRepository.findByProfileId(profile.getId()).stream().map(doc -> {
                DocumentDTO d = new DocumentDTO();
                d.id = doc.getId();
                d.name = doc.getFileName();
                d.url = doc.getUrl();
                d.type = doc.getType();
                return d;
            }).collect(Collectors.toList());
            dto.certificates = studentCertificateRepository.findByProfileId(profile.getId()).stream().map(cert -> {
                CertificateDTO c = new CertificateDTO();
                c.id = cert.getId();
                c.name = cert.getCertificateName();
                c.url = cert.getUrl();
                return c;
            }).collect(Collectors.toList());
            return dto;
        }).collect(Collectors.toList());

        // Map recruiter profiles to DTOs with documents
        List<RecruiterProfileDTO> recruiters = recruiterProfiles.stream().map(profile -> {
            RecruiterProfileDTO dto = new RecruiterProfileDTO();
            dto.id = profile.getId();
            dto.name = profile.getFirstName() + " " + profile.getLastName();
            dto.email = profile.getEmail();
            dto.company = profile.getCompanyName();
            dto.companyLogo = null; // Not present in model
            dto.phone = profile.getPhone();
            dto.companyWebsite = profile.getCompanyWebsite();
            dto.companyAddress = profile.getCompanyAddress();
            dto.companyDescription = profile.getCompanyDescription();
            dto.recruiterRole = profile.getRecruiterRole();
            dto.linkedinProfile = profile.getLinkedinProfile();
            dto.stinNumber = profile.getStinNumber();
            dto.documents = recruiterDocumentRepository.findAll().stream()
                    .filter(doc -> doc.getRecruiter() != null
                            && doc.getRecruiter().getEmail().equals(profile.getEmail()))
                    .map(doc -> {
                        DocumentDTO d = new DocumentDTO();
                        d.id = doc.getId();
                        d.name = doc.getFileName();
                        d.url = doc.getUrl();
                        d.type = doc.getType();
                        return d;
                    }).collect(Collectors.toList());
            return dto;
        }).collect(Collectors.toList());

        // Combine and return
        Map<String, Object> response = new HashMap<>();
        response.put("students", students);
        response.put("recruiters", recruiters);
        return ResponseEntity.ok(response);
    }
}