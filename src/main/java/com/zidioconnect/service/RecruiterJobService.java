package com.zidioconnect.service;

import com.zidioconnect.model.RecruiterJob;
import com.zidioconnect.model.Recruiter;
import com.zidioconnect.repository.RecruiterJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecruiterJobService {
    @Autowired
    private RecruiterJobRepository jobRepo;

    public RecruiterJob saveJob(RecruiterJob job) {
        return jobRepo.save(job);
    }

    public List<RecruiterJob> getJobsByRecruiter(Recruiter recruiter) {
        return jobRepo.findByRecruiter(recruiter);
    }

    public List<RecruiterJob> getAllJobs() {
        return jobRepo.findAll();
    }

    public RecruiterJob getJobById(Long id) {
        return jobRepo.findById(id).orElse(null);
    }

    public void deleteJob(Long id) {
        jobRepo.deleteById(id);
    }

    public List<RecruiterJob> getApprovedJobs() {
        return jobRepo.findByAdminApprovalStatus("APPROVED");
    }
}