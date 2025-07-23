package com.zidioconnect.controller;

import com.zidioconnect.dto.ApplicationRequest;
import com.zidioconnect.dto.ApplicationResponse;
import com.zidioconnect.model.Application;
import com.zidioconnect.model.StudentProfile;
import com.zidioconnect.model.RecruiterJob;
import com.zidioconnect.model.Recruiter;
import com.zidioconnect.service.ApplicationService;
import com.zidioconnect.repository.StudentProfileRepository;
import com.zidioconnect.repository.RecruiterJobRepository;
import com.zidioconnect.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Autowired
    private RecruiterJobRepository recruiterJobRepository;
    @Autowired
    private RecruiterRepository recruiterRepository;

    // Student applies to a job
    @PostMapping("/apply")
    public ResponseEntity<?> applyToJob(Authentication authentication, @RequestBody ApplicationRequest request) {
        String email = authentication.getName();
        StudentProfile profile = studentProfileRepository.findByStudent_Email(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body("Student profile not found");
        }
        Application application = applicationService.apply(request, profile.getId());
        return ResponseEntity.ok(application.getId());
    }

    // Student views their applications
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        String email = authentication.getName();
        StudentProfile profile = studentProfileRepository.findByStudent_Email(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body("Student profile not found");
        }
        List<Application> applications = applicationService.getApplicationsByStudent(profile.getId());
        return ResponseEntity.ok(applications.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    // Recruiter views applications for a job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsForJob(Authentication authentication, @PathVariable Long jobId) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        RecruiterJob job = recruiterJobRepository.findById(jobId).orElse(null);
        if (recruiter == null || job == null || !job.getRecruiter().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        List<Application> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(applications.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    // Recruiter updates application status (shortlist/reject)
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            Authentication authentication,
            @PathVariable Long applicationId,
            @RequestParam("status") String status) {
        // Optionally: Check recruiter ownership/authorization here
        Application app = applicationService.getApplicationById(applicationId);
        if (app == null) {
            return ResponseEntity.notFound().build();
        }
        app.setStatus(status);
        applicationService.save(app);
        return ResponseEntity.ok().build();
    }

    private ApplicationResponse toResponse(Application app) {
        ApplicationResponse resp = new ApplicationResponse();
        resp.id = app.getId();
        resp.jobId = app.getJob().getId();
        resp.jobTitle = app.getJob().getTitle();
        resp.studentProfileId = app.getStudentProfile().getId();
        resp.studentName = app.getStudentProfile().getFirstName() + " " + app.getStudentProfile().getLastName();
        resp.studentEmail = app.getStudentProfile().getEmail();
        // resp.phone = app.getStudentProfile().getPhone(); // REMOVE phone
        resp.college = app.getStudentProfile().getCollege();
        resp.course = app.getStudentProfile().getCourse();
        resp.yearOfStudy = app.getStudentProfile().getYearOfStudy();
        // Set skills as a List<String>
        resp.skills = app.getStudentProfile().getSkills() != null ? app.getStudentProfile().getSkills().stream()
                .map(s -> s.getSkill()).collect(java.util.stream.Collectors.toList())
                : java.util.Collections.emptyList();
        if (app.getResume() != null) {
            resp.resume = toDocInfo(app.getResume());
        }
        if (app.getMarksheet() != null) {
            resp.marksheet = toDocInfo(app.getMarksheet());
        }
        if (app.getCertificates() != null) {
            resp.certificates = app.getCertificates().stream().map(this::toCertInfo).collect(Collectors.toList());
        }
        resp.status = app.getStatus();
        resp.appliedAt = app.getAppliedAt();
        resp.questionForApplicant = app.getJob().getQuestionForApplicant();
        resp.answerForRecruiter = app.getAnswerForRecruiter();
        return resp;
    }

    private ApplicationResponse.DocumentInfo toDocInfo(com.zidioconnect.model.StudentDocument doc) {
        ApplicationResponse.DocumentInfo info = new ApplicationResponse.DocumentInfo();
        info.id = doc.getId();
        info.name = doc.getName();
        info.url = doc.getUrl();
        info.status = doc.getStatus();
        return info;
    }

    private ApplicationResponse.DocumentInfo toCertInfo(com.zidioconnect.model.StudentCertificate cert) {
        ApplicationResponse.DocumentInfo info = new ApplicationResponse.DocumentInfo();
        info.id = cert.getId();
        info.name = cert.getCertificateName();
        info.url = cert.getUrl();
        info.status = cert.getStatus();
        return info;
    }
}