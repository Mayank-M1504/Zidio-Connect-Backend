package com.zidioconnect.repository;

import com.zidioconnect.model.RecruiterJob;
import com.zidioconnect.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruiterJobRepository extends JpaRepository<RecruiterJob, Long> {
    List<RecruiterJob> findByRecruiter(Recruiter recruiter);
    List<RecruiterJob> findByAdminApprovalStatus(String adminApprovalStatus);
}