package com.zidioconnect.controller;

import com.zidioconnect.model.RecruiterJob;
import com.zidioconnect.model.Recruiter;
import com.zidioconnect.repository.RecruiterRepository;
import com.zidioconnect.service.RecruiterJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.zidioconnect.repository.RecruiterDocumentRepository;
import com.zidioconnect.model.RecruiterDocument;
import java.util.Arrays;

@RestController
@RequestMapping("/api/recruiter/jobs")
public class RecruiterJobController {
    @Autowired
    private RecruiterJobService jobService;
    @Autowired
    private RecruiterRepository recruiterRepo;
    @Autowired
    private RecruiterDocumentRepository recruiterDocumentRepository;

    // Add DTO for job listing with companyLogo and companyName
    static class JobWithCompanyDTO {
        public Long id;
        public String title;
        public String department;
        public String location;
        public String jobType;
        public String stipendSalary;
        public String duration;
        public String description;
        public String requirements;
        public String adminApprovalStatus;
        public String companyLogo;
        public String companyName;

        public JobWithCompanyDTO(RecruiterJob job) {
            this.id = job.getId();
            this.title = job.getTitle();
            this.department = job.getDepartment();
            this.location = job.getLocation();
            this.jobType = job.getJobType();
            this.stipendSalary = job.getStipendSalary();
            this.duration = job.getDuration();
            this.description = job.getDescription();
            this.requirements = job.getRequirements();
            this.adminApprovalStatus = job.getAdminApprovalStatus();
            this.companyLogo = job.getRecruiter() != null ? job.getRecruiter().getCompanyLogo() : null;
            this.companyName = job.getRecruiter() != null ? job.getRecruiter().getCompany() : null;
        }
    }

    @PostMapping
    public ResponseEntity<?> postJob(Authentication authentication, @RequestBody RecruiterJob job) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepo.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body("Recruiter not found");
        }
        // Profile completeness check (add/remove fields as needed)
        if (recruiter.getFirstName() == null || recruiter.getLastName() == null || recruiter.getEmail() == null
                || recruiter.getPhoneNumber() == null || recruiter.getCompany() == null) {
            return ResponseEntity.badRequest().body("Please complete your profile before posting a job.");
        }
        // Required document types
        String[] requiredDocs = { "registration", "gst", "pan", "business" };
        for (String docType : requiredDocs) {
            var docs = recruiterDocumentRepository.findByRecruiterAndTypeAndStatus(recruiter, docType, "APPROVED");
            if (docs == null || docs.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("All required documents must be uploaded and approved by admin before posting a job.");
            }
        }
        job.setRecruiter(recruiter);
        RecruiterJob saved = jobService.saveJob(job);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> getMyJobs(Authentication authentication) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepo.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body("Recruiter not found");
        }
        List<RecruiterJob> jobs = jobService.getJobsByRecruiter(recruiter);
        return ResponseEntity.ok(jobs);
    }

    // Endpoint for students to fetch only approved jobs
    @GetMapping("/approved")
    @PreAuthorize("hasRole('USER') or hasRole('STUDENT') or hasRole('RECRUITER')")
    public ResponseEntity<?> getApprovedJobs() {
        List<RecruiterJob> jobs = jobService.getApprovedJobs();
        return ResponseEntity.ok(jobs);
    }

    // In the job listing endpoint, map to JobWithCompanyDTO
    @GetMapping("/api/jobs")
    public ResponseEntity<?> getAllJobsWithCompany() {
        List<RecruiterJob> jobs = jobService.getAllJobs();
        List<JobWithCompanyDTO> jobsWithCompany = jobs.stream().map(JobWithCompanyDTO::new).toList();
        return ResponseEntity.ok(jobsWithCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepo.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body("Recruiter not found");
        }
        RecruiterJob job = jobService.getJobById(id);
        if (job == null || !job.getRecruiter().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(404).body("Job not found or not authorized");
        }
        jobService.deleteJob(id);
        return ResponseEntity.ok().body("Job deleted successfully");
    }

    // Add this endpoint for admin job approval/rejection
    @PatchMapping("/admin/approve/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveOrRejectJob(@PathVariable Long jobId, @RequestParam("status") String status) {
        RecruiterJob job = jobService.getJobById(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        job.setAdminApprovalStatus(status);
        jobService.saveJob(job);
        return ResponseEntity.ok(java.util.Map.of("message", "Job status updated", "jobId", jobId, "status", status));
    }
}