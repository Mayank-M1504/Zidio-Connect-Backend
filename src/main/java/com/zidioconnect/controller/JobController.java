package com.zidioconnect.controller;

import com.zidioconnect.model.RecruiterJob;
import com.zidioconnect.service.RecruiterJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private RecruiterJobService jobService;

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
        public String questionForApplicant;

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
            this.questionForApplicant = job.getQuestionForApplicant();
        }
    }

    @GetMapping
    public ResponseEntity<List<JobWithCompanyDTO>> getJobs(@RequestParam(value = "all", required = false) Boolean all) {
        List<RecruiterJob> jobs;
        if (all != null && all) {
            jobs = jobService.getAllJobs();
        } else {
            jobs = jobService.getApprovedJobs();
        }
        List<JobWithCompanyDTO> jobsWithCompany = jobs.stream().map(JobWithCompanyDTO::new).toList();
        return ResponseEntity.ok(jobsWithCompany);
    }
}